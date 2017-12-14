package bhw1899.paridae.model.tts;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

public class SpeakWord {
    private TextToSpeech ttsEnglish;
    private TextToSpeech ttsChinese;
    private Context context;
    private String word;
    private boolean on = false;
    private boolean hasChineseEngine = false;
    private int speakRepeatTimes = 1;
    private Thread speakThread;
    private SpeakCompleteListener listener;
    private String tag;
    private String str;
    private ArrayList<String> lines;
    private int maxSpeechLength = -1;

    private static SpeakWord instance = null;

    private SpeakWord() {
    }

    public static SpeakWord getInstance() {
        if (null == instance) {
            synchronized (SpeakWord.class) {
                if (null == instance) {
                    synchronized (SpeakWord.class) {
                        instance = new SpeakWord();
                    }
                }
            }
        }
        return instance;
    }

    private static final String ENGINE_GOOGLE = "com.google.android.tts";
    private static final String ENGINE_IFLYTEK = "com.iflytek.tts";

    public static final String DEFAULT_ENGLISH_ENGINE = ENGINE_GOOGLE;
    public static final String DEFAULT_CHINESE_ENGINE = ENGINE_IFLYTEK;

    public static final float CHINESE_SPEECH_RATE = 1.0f;

    public void init(Context contextIn) {
        init(contextIn, false);
    }

    public void init(Context contextIn, boolean onlyStartEnglishEngine) {
        context = contextIn;
        if (ttsEnglish != null) {
            return;
        }
        ttsEnglish = new TextToSpeech(context, new OnInitListener() {

            @Override
            public void onInit(int status) {
                if (TextToSpeech.SUCCESS != status) {
                    Toast.makeText(context, "TTS English init failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int result = ttsEnglish.setLanguage(Locale.UK);
                if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                        && result != TextToSpeech.LANG_AVAILABLE) {
                    Toast.makeText(context, "Language:UK NOT support!", Toast.LENGTH_SHORT).show();
                    return;
                }
                on = true;
            }
        }, DEFAULT_ENGLISH_ENGINE);
        maxSpeechLength = TextToSpeech.getMaxSpeechInputLength();
        if (onlyStartEnglishEngine) {
            return;
        }
        ttsChinese = new TextToSpeech(context, new OnInitListener() {

            @Override
            public void onInit(int status) {
                if (TextToSpeech.SUCCESS != status) {
                    Toast.makeText(context, "TTS Chinese init failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int result = ttsEnglish.setLanguage(Locale.UK);
                if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                        && result != TextToSpeech.LANG_AVAILABLE) {
                    Toast.makeText(context, "Language:UK NOT support!", Toast.LENGTH_SHORT).show();
                    return;
                }
                on = true;
                hasChineseEngine = true;
            }
        }, DEFAULT_CHINESE_ENGINE);
        ttsChinese.setSpeechRate(CHINESE_SPEECH_RATE);
    }

    @SuppressWarnings("deprecation")
    public void speakEnglishNow(String englishOnly) {
        if (on) {
            word = englishOnly;
            ttsEnglish.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @SuppressWarnings("deprecation")
    public void speakEnglish(String englishOnly) {
        if (on) {
            word = englishOnly;
            ttsEnglish.speak(word, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @SuppressWarnings("deprecation")
    public void speakChineseNow(String string) {
        if (on && hasChineseEngine) {
            word = string;
            ttsChinese.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @SuppressWarnings("deprecation")
    public void speakChinese(String string) {
        if (on && hasChineseEngine) {
            word = string;
            ttsChinese.speak(word, TextToSpeech.QUEUE_ADD, null);
        }
    }

    private int stopSpeaking() {
        return ttsEnglish.stop();
    }

    public void shutdown() {
        ttsEnglish.shutdown();
        if (hasChineseEngine) {
            ttsChinese.shutdown();
        }
        if (speakThread != null) {
            speakThread.interrupt();
        }
        on = false;
    }

    public interface SpeakCompleteListener {
        public void onComplete(String tag);
    }

    public void speakEnglish(String tag1, ArrayList<String> linesIn, int repeatTimes,
            SpeakCompleteListener l) {
        speakRepeatTimes = repeatTimes;
        listener = l;
        tag = tag1;
        lines = linesIn;
        synchronized (this) {
            if (on) {
                speakThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        for (String curLine : lines) {
                            for (int j = 0; j < speakRepeatTimes; j++) {
                                String strLeftover = curLine;
                                while (true) {
                                    String strNow = null;
                                    if (strLeftover.length() > maxSpeechLength) {
                                        int lastDotIndex = strLeftover.lastIndexOf(".",
                                                maxSpeechLength);
                                        if (lastDotIndex > 0) {
                                            strNow = strLeftover.substring(0, lastDotIndex);
                                            strLeftover = strLeftover.substring(lastDotIndex + 1,
                                                    strLeftover.length());
                                        } else {
                                            strNow = strLeftover.substring(0, maxSpeechLength);
                                            strLeftover = strLeftover.substring(
                                                    maxSpeechLength + 1, strLeftover.length());
                                        }
                                        ttsEnglish.speak(strNow, TextToSpeech.QUEUE_ADD, null);
                                    } else {
                                        strNow = strLeftover;
                                        ttsEnglish.speak(strNow, TextToSpeech.QUEUE_ADD, null);
                                        break;
                                    }
                                }
                            }

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        while (true) {
                            try {
                                if (!ttsEnglish.isSpeaking()) {
                                    break;
                                }
                                Thread.sleep(2000);
                                Thread.yield();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        if (listener != null) {
                            listener.onComplete(tag);
                        }
                    }
                });
                speakThread.start();
            }
        }
    }

    public void stop() {
        stopSpeaking();
        if (speakThread != null) {
            speakThread.interrupt();
        }
        if (listener != null) {
            listener.onComplete(null);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }
}
