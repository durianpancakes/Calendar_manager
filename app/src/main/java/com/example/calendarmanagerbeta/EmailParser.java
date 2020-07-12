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

    public ArrayList<Integer> TimeParse(String emailBody) {
        int startHour = 0;
        int startMinute = 0;
        int endHour = 0;
        int endMinute = 0;
        int foundInloop = 0;
        int viableDateCount = 0;
        ArrayList<Integer> Time = new ArrayList<>();
        // use java.util.calendar for Calendar mstarttime mendtime ?
        // uppercase the emailbody
        String UpperBody = emailBody.toUpperCase();
        //String text = "12:00 noon 14 May 2020, 5pm and 7pm, 5pm alone, at 4, 4 o'clock, 4o'clock, 04:00 PM, 5-7pm, 5-5:30pm, 11pm to 1am, there are 2 exams - chapter 1-9, 11.30am to 1.45pm";
        String text = "There will be a quiz at 12pm tomorrow";
        String text1 = text.toUpperCase();
        Pattern pattern = Pattern.compile("((AT|BY)[\\h])?\\b(1[0-2]|0?[1-9])([:.][0-5][0-9])?[\\h]?([AP][M])?[\\h]?(TO|AND|-|O'CLOCK)?\\b([\\h]?(1[0-2]|0?[1-9])([:.][0-5][0-9])?[\\h]?([AP][M])?)?");
        //\b(2359|23:59)\b
        Matcher matcher = pattern.matcher(text1);


        //if its a single digit, with no 'at' or no pm/am then ditch it
        // if its 1-9 with no at/pm/am then ditch
        // if it has to/and/-/o'clock then keep it

        while (matcher.find()) {

            String secondParse = matcher.group(0);
            System.out.println(secondParse + " this includes timings as well as random numbers.");

            startHour = 0;
            startMinute = 0;
            endHour = 0;
            endMinute = 0;
            foundInloop = 0;



            if(secondParse.contains("AM") || secondParse.contains("PM")) {
                viableDateCount++;
                String thirdParse = secondParse;
                System.out.println(thirdParse + " this should include am/pm ");
                Pattern pattern2 = Pattern.compile("(1[0-2]|0?[1-9])([:.][0-5][0-9])?");
                Matcher matcher2 = pattern2.matcher(thirdParse);
                // just the numbers eg 7:30-9pm -> 7:30 , 9

                boolean a = thirdParse.contains("AT");
                // start timing only? or might this be ambiguous
                boolean b = thirdParse.contains("-") || thirdParse.contains("AND") || thirdParse.contains("TO");
                // means there's a range , start and end timing
                boolean c = thirdParse.contains("BY");
                // end timing only?



                // Splittime function checks if the minute portion is ==0, if it is it wont return a valid start/endminute
                // right now start/endminute will be = 0 as initialized if they are useless 0s like 04:00, probably need to change this.


                if (thirdParse.contains("AM") && thirdParse.contains("PM")) {

                    //System.out.println(matcher2.group(0) + " this should only be time digits itself ");

                    // thirdParse will be :eg 11am to 1pm... / 11pm to 1am
                    // means it is definitely a range, don't have to check for a / b / c
                    // we dont need to user matcher2, we just cut up thirdParse.

                    // check if AM or PM comes first.
                    Pattern pattern3 = Pattern.compile("(1[0-2]|0?[1-9])([:.][0-5][0-9])?[\\h]?([AP][M])?");
                    // splits it into 11am, 1pm
                    Matcher matcher3 = pattern3.matcher(thirdParse);

                    System.out.println(thirdParse + " should be a range");

                    while(matcher3.find()) {
                        ArrayList<Integer> temp = splitTime(matcher3.group(0));
                        if(foundInloop == 0) {
                            //start timings

                            startHour = temp.get(0);

                            if(temp.size() == 2) {
                                startMinute = temp.get(1);
                            }

                            foundInloop++;

                        }
                        else if(foundInloop == 1) {
                            //end timings
                            endHour = temp.get(0);
                            if(temp.size() == 2) {
                                endMinute = temp.get(1);
                            }
                            foundInloop = 0;


                        }


                    }
                } else if (thirdParse.contains("AM")) {

                    if (b) {
                        while(matcher2.find()) {
                            System.out.println(thirdParse + " should be a range");
                            // find 2 groups, eg 7:30-9pm -> 7:30 , 9
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));

                            if(foundInloop == 0) {
                                startHour = temp.get(0);
                                if (temp.size() == 2) {
                                    startMinute = temp.get(1);
                                }
                                foundInloop++;
                            }
                            else if(foundInloop == 1) {
                                endHour = temp.get(0);
                                if(temp.size() == 2) {
                                    endMinute = temp.get(1);
                                }
                                foundInloop = 0;
                            }
                        }
                        // means time range, with start and end time. could include minutes....

                    } else if (c) {
                        // should only be 1 number since its eg. by 7
                        if(matcher2.find()) {
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));
                            endHour = temp.get(0);
                            if(temp.size() == 2) {
                                endMinute = temp.get(1);
                            }

                        }
                        // end time only

                    } else if (a) {
                        // at 7 (am)
                        //start time only

                        if(matcher2.find()) {
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));
                            startHour = temp.get(0);
                            if(temp.size() == 2) {
                                startMinute = temp.get(1);
                            }
                        }

                    } else {
                        // 04:00 PM , no at,by,or range indicator
                        // sets startime to 16:00 immediately
                        if(matcher2.find()) {
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));
                            startHour = temp.get(0);
                            if(temp.size() == 2) {
                                startMinute = temp.get(1);
                            }
                        }

                    }


                } else if (thirdParse.contains("PM")) {

                    if (b) {
                        System.out.println(thirdParse + " should be a range");
                        while(matcher2.find()) {
                            // crashes here.....
                            // find 2 groups, eg 7:30-9pm -> 7:30 , 9
                            System.out.println(matcher2.group(0));
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));

                            if(foundInloop == 0) {
                                startHour = temp.get(0);
                                if(startHour != 12) {
                                    startHour = startHour + 12;
                                    // to fit 24hr clock
                                }

                                if (temp.size() == 2) {
                                    startMinute = temp.get(1);
                                }
                                foundInloop++;
                            }
                            else if(foundInloop == 1) {
                                endHour = temp.get(0);
                                if(endHour != 12) {
                                    endHour = endHour + 12;
                                }
                                if(temp.size() == 2) {
                                    endMinute = temp.get(1);
                                }
                                foundInloop = 0;
                            }

                            //System.out.println("b is done");
                        }
                        // means time range, with start and end time. could include minutes....

                    } else if (c) {
                        // should only be 1 number since its eg. by 7
                        if(matcher2.find()) {
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));

                            endHour = temp.get(0);
                            if(endHour != 12) {
                                endHour = endHour + 12;
                            }

                            if(temp.size() == 2) {
                                endMinute = temp.get(1);
                            }

                        }
                        // end time only

                    } else if (a) {
                        // at 7 (am)
                        //start time only

                        if(matcher2.find()) {
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));
                            startHour = temp.get(0);
                            if(startHour != 12) {
                                startHour = startHour + 12;
                            }
                            if(temp.size() == 2) {
                                startMinute = temp.get(1);
                            }
                        }

                    } else {
                        if(matcher2.find()) {
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));
                            startHour = temp.get(0);
                            if(startHour != 12) {
                                startHour = startHour + 12;
                            }
                            if(temp.size() == 2) {
                                startMinute = temp.get(1);
                            }
                        }

                    }

                } else {
                    //thirdparse doesnt have am/pm. eg At 4 or 4 o'clock
                    System.out.println("Ambiguous timing (no am/pm) but has at/oclock/by : " + thirdParse);
                }


                // Remember to add start and end timings into Time to return to main.
                // also need to implement the june 08, 2020 etc in dateparse.

                System.out.println("startHour is " + startHour + " startMinute is "+ startMinute + " to " + " endHour is : " +
                        endHour + " endMinute is " + endMinute);


            }
            else if (secondParse.contains(":") || secondParse.contains(".")) {
                viableDateCount++;
                // no at/by/range
                String thirdParse = secondParse;
                //eg 14:00 or 12:00
                // should be 24hrclock
                System.out.println(thirdParse + " this should only include : or . ");
                Pattern pattern2 = Pattern.compile("(1[0-2]|0?[1-9])([:.][0-5][0-9])?");
                Matcher matcher2 = pattern2.matcher(thirdParse);


                boolean a = thirdParse.contains("AT");
                // start timing only? or might this be ambiguous
                boolean b = thirdParse.contains("-") || thirdParse.contains("AND") || thirdParse.contains("TO");
                // means there's a range , start and end timing
                boolean c = thirdParse.contains("BY");
                // end timing only?

                if (b) {
                    System.out.println(thirdParse + " should be a range");
                    while(matcher2.find()) {
                        // find 2 groups, eg 7:30-9pm -> 7:30 , 9
                        ArrayList<Integer> temp = splitTime(matcher2.group(0));

                        if(foundInloop == 0) {
                            startHour = temp.get(0);

                            if (temp.size() == 2) {
                                startMinute = temp.get(1);
                            }
                            foundInloop++;
                        }
                        else if(foundInloop == 1) {
                            endHour = temp.get(0);

                            if(temp.size() == 2) {
                                endMinute = temp.get(1);
                            }
                            foundInloop = 0;
                        }
                    }
                    // means time range, with start and end time. could include minutes....

                } else if (c) {
                    // should only be 1 number since its eg. by 7
                    if(matcher2.find()) {
                        ArrayList<Integer> temp = splitTime(matcher2.group(0));

                        endHour = temp.get(0);

                        if(temp.size() == 2) {
                            endMinute = temp.get(1);
                        }

                    }
                    // end time only

                } else if (a) {
                    // at 7 (am)
                    //start time only

                    if(matcher2.find()) {
                        ArrayList<Integer> temp = splitTime(matcher2.group(0));
                        startHour = temp.get(0);
                        if(temp.size() == 2) {
                            startMinute = temp.get(1);
                        }
                    }

                }
                else {
                    if(matcher2.find()) {
                        ArrayList<Integer> temp = splitTime(matcher2.group(0));

                        startHour = temp.get(0);
                        if(temp.size() == 2) {
                            startMinute = temp.get(1);
                        }
                    }

                }



                System.out.println(startHour + " "+ startMinute + " to " + endHour + " " + endMinute);
            }

            else if (secondParse.contains("O'CLOCK")){
                viableDateCount++;
                System.out.println(secondParse + " contains o'clock, ambiguous");

            }
            else if (secondParse.contains("AT") || secondParse.contains("BY")) {
                viableDateCount++;
                System.out.println(secondParse + " contains AT or BY but no AM/PM, ambiguous");
            }
            else {
                System.out.println(secondParse + " is a false parse.");
            }


        }


        // 2359 special case since normally only 12 hour clock is parsed.
        if(emailBody.toUpperCase().contains("2359") || emailBody.toUpperCase().contains("23:59")) {
            viableDateCount++;
            if(emailBody.toUpperCase().contains("BY 2359") || emailBody.toUpperCase().contains("BY 23:59")) {
                endHour = 23;
                endMinute = 59;
            } else if (emailBody.toUpperCase().contains("AT 2359") || emailBody.toUpperCase().contains("AT 23:59")) {
                startHour = 23;
                startMinute = 59;
            }

        }

        if(viableDateCount > 1) {
            System.out.println("There are more than 1 viable dates in this string.");

        } else if(viableDateCount == 1) {
            Time.add(startHour);
            Time.add(startMinute);
            Time.add(endMinute);
            Time.add(endHour);

        }


        return Time;


    }


    public String EventParse(String emailBody) {
        String eventType = new String();

        Pattern pattern = Pattern.compile("(QUIZ|TEST|EXAM)");
        // incomplete, may need add more stuff
        Matcher matcher = pattern.matcher(emailBody.toUpperCase());

        while(matcher.find()) {
            eventType = matcher.group(0);
        }


        return eventType;
    }

    public ArrayList<Integer> splitTime(String time) {
        //time here will be just integers like 07:40 or 7 or 12:30
        ArrayList<Integer> timeStorage = new ArrayList<>();
        Pattern pattern = Pattern.compile("(1[0-2]|0?[1-9])");
        Pattern pattern1 = Pattern.compile("[:.][0-5][0-9]");
        Matcher matcher = pattern.matcher(time);
        Matcher matcher1 = pattern1.matcher(time);
        System.out.println("In splitTime, : " + time);
        int hour = 0;
        int minute = 0;
        if(time.contains(".") || time.contains(":")) {
            // the pm am is already considered in TimeParse code i think
            System.out.println("contains : or .");

            while(matcher.find() && matcher1.find()) {
                //should this be ||?

                //System.out.println(matcher.group(0));

                if (matcher.group(0).startsWith("0")) {
                    hour = Integer.parseInt(matcher.group(0).substring(1));

                } else {
                    hour = Integer.parseInt(matcher.group(0));
                    if(time.contains("PM")) {
                        hour = hour + 12;
                    }
                }
                //System.out.println(hour);

                minute = Integer.parseInt(matcher1.group(0).substring(1));
                // to skip : or .


                timeStorage.add(hour);

                if (minute != 0) {
                    timeStorage.add(minute);
                }

            }

        }
        else if (time.contains("PM") || time.contains("AM")) {

            // but no . or :
            while(matcher.find()) {
                if(time.contains("PM")) {
                    if (matcher.group(0).startsWith("0")) {
                        hour = Integer.parseInt(matcher.group(0).substring(1));

                    } else {
                        hour = Integer.parseInt(matcher.group(0));
                        hour = hour + 12;
                    }

                }
                else if(time.contains("AM")) {
                    if (matcher.group(0).startsWith("0")) {
                        hour = Integer.parseInt(matcher.group(0).substring(1));

                    } else {
                        hour = Integer.parseInt(matcher.group(0));
                    }


                }

                timeStorage.add(hour);
            }

        }
        else {
            // just hour, no minutes, no am/pm
            if(time.startsWith("0")) {
                timeStorage.add(Integer.parseInt(time.substring(1)));
            }
            else {
                timeStorage.add(Integer.parseInt(time));
            }

        }

        System.out.println(timeStorage.get(0) + " at end of splittime");

        return timeStorage;


    }



}

