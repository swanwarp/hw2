package ru.ifmo.android_2015.citycam;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.ifmo.android_2015.citycam.webcams.Webcams;

/**
 * Парсер JSON для вебкамер
 */
final class JsonParse {

    /**
     * метод, который ищет первое вхождение имени "webcam" в JSON файле и возвращает массив камер.
     */
    static ArrayList JsonReadWebcams(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.beginObject();

        try {
            while(reader.hasNext() && reader.peek() != JsonToken.BEGIN_OBJECT) {
                reader.skipValue();
            }
            reader.beginObject();
            while(reader.hasNext()) {
                if(!reader.nextName().equals("webcam")) {
                    reader.skipValue();
                } else break;
            }

            if(reader.hasNext()) {
                return webArray(reader);
            } else {
                return null;
            }

        } finally {
            reader.close();
        }
    }

    /**
     * собственно, читаем информацию о камерах и создаем массив камер
     */
    static private ArrayList<Webcam> webArray(JsonReader reader) throws IOException{
        ArrayList<Webcam> webcams = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            webcams.add(createWebcam(reader));
        }
        reader.endArray();
        return webcams;
    }

    /**
     * парсим информацию об отдельно взятой вебке
     */
    static private Webcam createWebcam(JsonReader reader) throws IOException {
        reader.beginObject();

        String city = "", country = "", name = "", url = "";


        while(reader.hasNext()) {
            switch (reader.nextName()) {
                case "city": city = reader.nextString(); break;

                case "country": country = reader.nextString(); break;

                case "title": name = reader.nextString(); break;

                case "preview_url": url = reader.nextString(); break;

                default: reader.skipValue(); break;
            }
        }

        reader.endObject();

        return new Webcam(name, city, country, url);
    }
}
