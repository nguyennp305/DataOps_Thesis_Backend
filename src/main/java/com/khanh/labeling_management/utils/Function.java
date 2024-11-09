package com.khanh.labeling_management.utils;

import com.khanh.labeling_management.config.Constants;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Function {
    public static Date convertDateToUTC7(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 7);

        return calendar.getTime();
    }

    public static String randomCharacter(int numberCharacter) {
        String saltChars = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < numberCharacter) { // length of the random string.
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static String formatDate(Date date, String pattern){
        try {
            return new SimpleDateFormat(pattern).format(date);
        }catch (Exception ex){
            return null;
        }

    }

    private static String formatDatePattern = "yyyy-MM-dd HH:mm:ss";

    public static String getVersionName(double versionNumber){
        String result = "";
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String tempStr = decimalFormat.format(versionNumber);
        String[] tempArr = tempStr.split("\\.");
        if (tempArr.length == 1) result = "v" + tempStr + ".0.0";
        else if (tempArr.length == 2){
            if (tempArr[1].length() == 1) result = "v" + tempArr[0] + "." + tempArr[1] + ".0";
            else  result = "v" + tempArr[0] + "." + tempArr[1].substring(0,1) + "." + tempArr[1].substring(1);
        }
        return result;
    }

    public static Date parseDate(String date){
        try {
            if (!Function.checkValidText(date)) return null;
            else return new SimpleDateFormat(formatDatePattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDate(String date,String formatDatePattern){
        try {
            return new SimpleDateFormat(formatDatePattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkValidText(String text) {
        return text != null && !text.trim().equals("") && !text.trim().equals("null") && !text.trim().equals("{}");
    }

    public static  String formatTextTesting(String text){
        String result = "";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            text = text.replaceAll("[!#$'()*+,:;<=>@\\[\\]^`{}~?.]", Constants.SPACE);
            String[] wordArr = text.split(Constants.SPACE);
            for (int i = 0; i < wordArr.length ; i++){
                if (Function.checkValidText(wordArr[i])) stringBuilder.append(wordArr[i].trim()).append(Constants.SPACE);
            }
            result = stringBuilder.toString().trim();
        }catch (Exception ex){

        }
        return result;
    }

    public static boolean isNumber(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }


    public static Boolean checkBoolean(Long isTraining) {
        return isTraining == 1;
    }

    public static void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

    public static boolean saveJsonFile(String path, String content){
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.write(content);
            return true;
        } catch (Exception e) {
            System.out.println("Error when save file: "+ path);
            e.printStackTrace();
        }
        return false;
    }

    public static Date getFirstDay(Date date,int day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        try {
            cal.add(Calendar.DATE,day);
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTime();
        }catch (Exception ex){
            return null;
        }
    }

    public static Date getLastDay(Date date,int day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        try {
            cal.add(Calendar.DATE,day);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            return cal.getTime();
        }catch (Exception ex){
            return null;
        }
    }
}
