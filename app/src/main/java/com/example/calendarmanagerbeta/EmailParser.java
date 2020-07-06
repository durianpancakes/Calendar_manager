package com.example.calendarmanagerbeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailParser {


    public void DateParse(String emailBody) {
        //regex tester
        String test = "The quiz will be held on 12 Feb and 12th Feb and 12 Feb 2020, and 12th Feb 2020 and 12th May, 2020 and 51 May and 10-Aug-2020 ";
        Pattern pattern = Pattern.compile("\\b(([0]?[0-9])|([0-2][0-9])|([3][0-1]))(st|nd|rd|th)?\\b[\\h-](Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec),?([\\h-]\\d{4})?");
        // what if they wrote january/february/march -> add to original pattern
        // caps problems too -> convert whole test string to caps? -> change th/st/rd/nd into caps, month names too. second month pattern should be able to pull short form out.
        Matcher matcher = pattern.matcher(test);

        while (matcher.find()) {
            System.out.println(matcher.group(0));
            String date = matcher.group(0);
            String subDate = date.substring(0,2);

            Pattern pattern1 = Pattern.compile("(([0]?[0-9])|([0-2][0-9])|([3][0-1])){2}");
            Pattern pattern2 = Pattern.compile("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)");
            Pattern pattern3 = Pattern.compile("\\d{4}");
            Matcher matcher1 = pattern1.matcher(subDate);
            Matcher matcher2 = pattern2.matcher(date);
            Matcher matcher3 = pattern3.matcher(date);
            while(matcher1.find()) {
                System.out.println(matcher1.group(0));
            }
            while(matcher2.find()) {
                System.out.println(matcher2.group(0));
            }
            while(matcher3.find()) {
                System.out.println(matcher3.group(0));
            }


        }

        //weekviewevent format
        //graph helper ask for body
        // in emailist in emailfragment parse around line75



    }

}

