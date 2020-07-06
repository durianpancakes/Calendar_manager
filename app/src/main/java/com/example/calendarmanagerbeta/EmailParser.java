package com.example.calendarmanagerbeta;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailParser {


    public ArrayList<Integer> DateParse(String emailBody) {
        int date = 0;
        int month = 0;
        int year = 0;
        int DMYcount = 0;
        ArrayList<Integer> DMY = new ArrayList<>();

        String UpperBody = emailBody.toUpperCase();
        //regex tester
        //String test = "The quiz will be held on 09 Feb and 12th February and 12 Feb 2020, and 12th Feb 2020 and 12th May, 2020 and 51 May and 10-Aug-2020 ";
        //String test1 = test.toUpperCase();
        Pattern pattern = Pattern.compile("\\b(([0]?[0-9])|([0-2][0-9])|([3][0-1]))(ST|ND|RD|TH)?\\b[\\h-](JAN|JANUARY|FEB|FEBRUARY|MAR|MARCH|APR|APRIL|MAY|JUN|JUNE|JUL|JULY|AUG|AUGUST|SEP|SEPTEMBER|OCT|OCTOBER|NOV|NOVEMBER|DEC|DECEMBER),?([\\h-]\\d{4})?");
        // what if they wrote january/february/march -> add to original pattern
        // caps problems too -> convert whole test string to caps? -> change th/st/rd/nd into caps, month names too. second month pattern should be able to pull short form out.
        Matcher matcher = pattern.matcher(UpperBody);

        while (matcher.find()) {
            DMYcount++;
            if(DMYcount > 1) {
                //means there is more than 1 date found.. not sure what to do yet
            }
            System.out.println(matcher.group(0));


            String subDate = matcher.group(0).substring(0,2);

            Pattern pattern1 = Pattern.compile("(([0]?[0-9])|([0-2][0-9])|([3][0-1])){2}");
            // leading zero date possible
            Pattern pattern2 = Pattern.compile("(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)");
            Pattern pattern3 = Pattern.compile("\\d{4}");
            Matcher matcher1 = pattern1.matcher(subDate);
            Matcher matcher2 = pattern2.matcher(matcher.group(0));
            Matcher matcher3 = pattern3.matcher(matcher.group(0));
            while(matcher1.find()) {
                if(matcher1.group(0).startsWith("0")) {
                    date = Integer.parseInt(matcher1.group(0).substring(1));
                }
                else {
                    date = Integer.parseInt(matcher1.group(0));
                }
                System.out.println( date );
                DMY.add(date);
                //fixed
            }
            while(matcher2.find()) {
                switch(matcher2.group(0)) {
                    case "JAN":
                        month = 1;
                        break;
                    case "FEB":
                        month = 2;
                        break;
                    case "MAR":
                        month = 3;
                        break;
                    case "APR":
                        month = 4;
                        break;
                    case "MAY":
                        month = 5;
                        break;
                    case "JUN":
                        month = 6;
                        break;
                    case "JUL":
                        month = 7;
                        break;
                    case "AUG":
                        month = 8;
                        break;
                    case "SEP":
                        month = 9;
                        break;
                    case "OCT":
                        month = 10;
                        break;
                    case "NOV":
                        month = 11;
                        break;
                    case "DEC":
                        month = 12;
                        break;
                    default:
                        break;
                }
                System.out.println("Month Number is " + month + ", " + matcher2.group(0));
                //works
                DMY.add(month);

            }
            while(matcher3.find()) {
                year = Integer.parseInt(matcher3.group(0));
                System.out.println(year);
                //works
                DMY.add(year);

            }

        }

        return DMY;

        //weekviewevent format
        // in emailist in emailfragment parse around line75







    }

}

