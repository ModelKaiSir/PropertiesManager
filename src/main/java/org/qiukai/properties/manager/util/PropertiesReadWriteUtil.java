package org.qiukai.properties.manager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.StringJoiner;

public class PropertiesReadWriteUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(PropertiesReadWriteUtil.class);

    public static void reads(String propertiesPath, ReadLine readLine) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(propertiesPath), "utf-8"))) {

            String local;
            while ((local = reader.readLine()) != null) {
                readLine.readLine(local);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void writes(String propertiesPath, String properties) {

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propertiesPath), "utf-8"))) {

            writer.write(properties);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void writes(String propertiesPath, List<String> properties) {

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propertiesPath), "utf-8"))) {

            for (String p : properties) {

                writer.newLine();
                writer.write(p);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {

        StringJoiner joiner = new StringJoiner("\n");
        PropertiesReadWriteUtil.reads("D:/config.properites", str -> {

            if (!str.isEmpty()) {
                joiner.add(str);
            }
        });

        PropertiesReadWriteUtil.writes("D:/config2.properites", joiner.toString());
    }

    public static interface ReadLine {

        void readLine(String line);
    }
}
