package com.example.calendarmanagerbeta;

import android.content.Context;

import androidx.annotation.NonNull;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailParser {
    private ParserCallback mCallback;
    private static EmailParser INSTANCE = null;
    private Context mContext;

    public EmailParser(Context context) {
        this.mContext = context;
    }

    public void setmCallback(ParserCallback callback) {
        mCallback = callback;
    }

    public static synchronized EmailParser getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new EmailParser(context);
        }
        return INSTANCE;
    }

    public void AllParse(String emailBody, String emailSubject, String emailWeblink) {
        System.out.println("Allparse is called");

        boolean startHourSet = false;
        boolean endHourSet = false;

        ArrayList<Integer> DMY = DateParse(emailBody);
        ArrayList<Integer> Time = TimeParse(emailBody);

        int datecount = 0;


        WeekViewEvent event = new WeekViewEvent();
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        if (DMY.get(0) != 99) {
            startTime.set(Calendar.DAY_OF_MONTH, DMY.get(0));
            endTime.set(Calendar.DAY_OF_MONTH, DMY.get(0));
            System.out.println(DMY.get(0) + " start day/end day added");
            datecount++;

        }
        if (DMY.get(1) != 99) {
            startTime.set(Calendar.MONTH, DMY.get(1));
            endTime.set(Calendar.MONTH, DMY.get(1));
            System.out.println(DMY.get(1) + " start mth/end mth added");
            datecount++;
        }
        if (DMY.get(2) != 99) {
            startTime.set(Calendar.YEAR, DMY.get(2));
            endTime.set(Calendar.YEAR, DMY.get(2));
            System.out.println(DMY.get(2) + " start yr/end yr added");
            datecount++;
        }
        if (Time.get(0) < 24 ) {
            startTime.set(Calendar.HOUR_OF_DAY, Time.get(0));
            startHourSet = true;
            if (Time.get(1) < 60 ) {
                startTime.set(Calendar.MINUTE, Time.get(1));
            } else {
                startTime.set(Calendar.MINUTE, 0);
            }
        }

        if (Time.get(2) < 24 ) {
            System.out.println("ENTERED" + Time.get(2));
            endTime.set(Calendar.HOUR_OF_DAY, Time.get(2));
            endHourSet = true;
            if (Time.get(3) < 60) {
                System.out.println("ENTERED" + Time.get(3));
                endTime.set(Calendar.MINUTE, Time.get(3));
            } else {
                startTime.set(Calendar.MINUTE, 0);
            }
        }

        event.setDescription("this event was created by the email parser \n " + "--------------------- \n \n \n " + emailBody);

        event.setName(emailSubject);
        event.setmWeblink(emailWeblink);
        event.setLocation("parsed");

        // 4 cases
        // 1, if there is start time but no end time - > 10mins
        // 2, if there is end but no start -> 10mins b4
        // 3, if there is start and end
        // 4, if there is no start and no end.
        if(startHourSet && endHourSet) {
            System.out.println("start and end hour set");
        }
        else if(startHourSet && !endHourSet) {
            System.out.println("only start hour set");
            endTime.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
            endTime.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY) + 1);
            event.setDescription("this event was created by the email parser \n" + "end hour undetected so it ends 1 hour after start time \n" + "--------------------- \n \n \n " + emailBody);
        }
        else if(!startHourSet && endHourSet) {
            System.out.println("only end hour set");
            startTime.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
            startTime.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY) - 1);
            event.setDescription("this event was created by the email parser \n \n \n" + "start hour undetected so it starts 1 hour before the end time \n " + "--------------------- \n \n \n " + emailBody);
        }
        else if(!startHourSet && !endHourSet) {
            // or too many timings
            startTime.set(Calendar.HOUR_OF_DAY, 10);
            startTime.set(Calendar.MINUTE, 0);
            endTime.set(Calendar.HOUR_OF_DAY, 12);
            endTime.set(Calendar.MINUTE, 0);
            event.setLocation("parsed, All day");
            if(Time.get(0) == 586) {
                event.setDescription("this event was created by the email parser \n" + "event is added as all-day because there were too many timings \n"
                        + "--------------------- \n \n \n " + emailBody);

            }
            else {
                event.setDescription("this event was created by the email parser \n" + "event is added as all-day because there were no timings  \n" + "--------------------- \n \n \n " + emailBody);
            }
            event.setAllDay(true);
        }

        event.setStartTime(startTime);
        event.setEndTime(endTime);

        if(datecount == 3) {
            if (mCallback != null) {
                System.out.println("datecount 3 :" + event.getStartTime().get(Calendar.DAY_OF_MONTH) + event.getStartTime().get(Calendar.MONTH) + event.getStartTime().get(Calendar.YEAR)
                + event.getStartTime().get(Calendar.HOUR_OF_DAY) + event.getStartTime().get(Calendar.MINUTE));
                mCallback.onEventAdded(event);
                System.out.println("mparsercallback called");
            } else {
                System.out.println("null mCallback");
            }

        }
        else if(datecount == 2) {
            // no year
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            if (mCallback != null) {
                mCallback.onEventAdded(event);
                System.out.println("mparsercallback called");
            } else {
                System.out.println("null mCallback");
            }
        }
    }


    public ArrayList<Integer> DateParse(String emailBody) {
        int date = 99;
        int month = 99;
        int year = 99;
        int DMYcount = 0;
        ArrayList<Integer> DMY = new ArrayList<>();

        String UpperBody = emailBody.toUpperCase();
        //regex tester
        Pattern pattern = Pattern.compile("\\b(([0]?[0-9])|([0-2][0-9])|([3][0-1]))(ST|ND|RD|TH)?[\\h-](JAN|JANUARY|FEB|FEBRUARY|MAR|MARCH|APR|APRIL|MAY|JUN|JUNE|JUL|JULY|AUG|AUGUST|SEP|SEPTEMBER|OCT|OCTOBER|NOV|NOVEMBER|DEC|DECEMBER),?([\\h-]\\d{4})?\\b");
        Pattern patternx = Pattern.compile("\\b(JAN|JANUARY|FEB|FEBRUARY|MAR|MARCH|APR|APRIL|MAY|JUN|JUNE|JUL|JULY|AUG|AUGUST|SEP|SEPTEMBER|OCT|OCTOBER|NOV|NOVEMBER|DEC|DECEMBER)[\\h-](([0]?[0-9])|([0-2][0-9])|([3][0-1]))(ST|ND|RD|TH)?,?([\\h-]\\d{4})?\\b");
        //(([0]?[0-9])|([0-2][0-9])|([3][0-1]))(ST|ND|RD|TH)?
        Matcher matcher = pattern.matcher(UpperBody);
        Matcher matcherx = patternx.matcher(UpperBody);

        while (matcher.find()) {
            DMYcount++;
            if (DMYcount > 1) {
                System.out.println("More than 1 date found. reject/ambiguous");
                break;
            }
            System.out.println(matcher.group(0));


            String subDate = matcher.group(0).substring(0, 2);

            Pattern pattern1 = Pattern.compile("(([0]?[0-9])|([0-2][0-9])|([3][0-1])){2}");
            Pattern pattern2 = Pattern.compile("(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)");
            Pattern pattern3 = Pattern.compile("\\d{4}");
            Matcher matcher1 = pattern1.matcher(subDate);
            Matcher matcher2 = pattern2.matcher(matcher.group(0));
            Matcher matcher3 = pattern3.matcher(matcher.group(0));
            while (matcher1.find()) {
                if (matcher1.group(0).startsWith("0")) {
                    date = Integer.parseInt(matcher1.group(0).substring(1));
                } else {
                    date = Integer.parseInt(matcher1.group(0));
                }
                System.out.println(date);
            }
            while (matcher2.find()) {
                switch (matcher2.group(0)) {
                    case "JAN":
                        month = Calendar.JANUARY;
                        break;
                    case "FEB":
                        month = Calendar.FEBRUARY;
                        break;
                    case "MAR":
                        month = Calendar.MARCH;
                        break;
                    case "APR":
                        month = Calendar.APRIL;
                        break;
                    case "MAY":
                        month = Calendar.MAY;
                        break;
                    case "JUN":
                        month = Calendar.JUNE;
                        break;
                    case "JUL":
                        month = Calendar.JULY;
                        break;
                    case "AUG":
                        month = Calendar.AUGUST;
                        break;
                    case "SEP":
                        month = Calendar.SEPTEMBER;
                        break;
                    case "OCT":
                        month = Calendar.OCTOBER;
                        break;
                    case "NOV":
                        month = Calendar.NOVEMBER;
                        break;
                    case "DEC":
                        month = Calendar.DECEMBER;
                        break;
                    default:
                        break;
                }
                System.out.println("Month Number is " + month + ", " + matcher2.group(0));
            }
            while (matcher3.find()) {
                year = Integer.parseInt(matcher3.group(0));
                System.out.println(year);
            }
            DMY.add(date);
            DMY.add(month);
            DMY.add(year);

        }

        if (DMYcount == 0) {
            while (matcherx.find()) {
                DMYcount++;
                if (DMYcount > 1) {
                    System.out.println("More than 1 date found. reject/ambiguous");
                    break;
                }

                Pattern pattern1 = Pattern.compile("\\b(([0]?[0-9])|([0-2][0-9])|([3][0-1])){2}\\b");
                // leading zero date possible
                Pattern pattern2 = Pattern.compile("(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)");
                Pattern pattern3 = Pattern.compile("\\d{4}");
                Matcher matcher1 = pattern1.matcher(matcherx.group(0));
                Matcher matcher2 = pattern2.matcher(matcherx.group(0));
                Matcher matcher3 = pattern3.matcher(matcherx.group(0));
                while (matcher1.find()) {
                    if (matcher1.group(0).startsWith("0")) {
                        date = Integer.parseInt(matcher1.group(0).substring(1));
                    } else {
                        date = Integer.parseInt(matcher1.group(0));
                    }
                    System.out.println(date);
                }
                while (matcher2.find()) {
                    switch (matcher2.group(0)) {
                        case "JAN":
                            month = Calendar.JANUARY;
                            break;
                        case "FEB":
                            month = Calendar.FEBRUARY;
                            break;
                        case "MAR":
                            month = Calendar.MARCH;
                            break;
                        case "APR":
                            month = Calendar.APRIL;
                            break;
                        case "MAY":
                            month = Calendar.MAY;
                            break;
                        case "JUN":
                            month = Calendar.JUNE;
                            break;
                        case "JUL":
                            month = Calendar.JULY;
                            break;
                        case "AUG":
                            month = Calendar.AUGUST;
                            break;
                        case "SEP":
                            month = Calendar.SEPTEMBER;
                            break;
                        case "OCT":
                            month = Calendar.OCTOBER;
                            break;
                        case "NOV":
                            month = Calendar.NOVEMBER;
                            break;
                        case "DEC":
                            month = Calendar.DECEMBER;
                            break;
                        default:
                            break;
                    }
                    System.out.println("Month Number is " + month + ", " + matcher2.group(0));
                }
                while (matcher3.find()) {
                    year = Integer.parseInt(matcher3.group(0));
                    System.out.println(year);
                }
                DMY.add(date);
                DMY.add(month);
                DMY.add(year);

            }
        }

        // if there are no dates found
        if (DMYcount == 0) {
            DMY.add(99);
            DMY.add(99);
            DMY.add(99);

        }
        else if(DMYcount > 1) {
            //unused because the event wont even be created
            DMY.add(586);
            DMY.add(586);
            DMY.add(586);
        }
        return DMY;
    }

    public ArrayList<Integer> TimeParse(String emailBody) {
        int startHour = 99;
        int startMinute = 99;
        int endHour = 99;
        int endMinute = 99;
        int foundInloop = 0;
        int viableTimeCount = 0;
        ArrayList<Integer> Time = new ArrayList<>();
        //String UpperBody = emailBody.toUpperCase();
        //String text = "12:00 noon 14 May 2020, 5pm and 7pm, 5pm alone, at 4, 4 o'clock, 4o'clock, 04:00 PM, 5-7pm, 5-5:30pm, 11pm to 1am, there are 2 exams - chapter 1-9, 11.30am to 1.45pm";
        //String text = "There will be a quiz at 3.55 - 4.15pm tomorrow";
        String text1 = emailBody.toUpperCase();
        Pattern pattern = Pattern.compile("((AT|BY)[\\h])?(\\$)?\\b\\b(1[0-9]|0?[0-9]|2[0-3])([:.][0-5][0-9])?[\\h]?([AP][M])?\\b[\\h]?(TO|AND|-|O'CLOCK)?([\\h]?(1[0-9]|0?[0-9]|2[0-3])([:.][0-5][0-9])?[\\h]?([AP][M])?)?\\b");
        // old without 24h Pattern pattern = Pattern.compile("((AT|BY)[\\h])?\\b\\b(1[0-2]|0?[1-9])([:.][0-5][0-9])?[\\h]?([AP][M])?\\b[\\h]?(TO|AND|-|O'CLOCK)?([\\h]?(1[0-2]|0?[1-9])([:.][0-5][0-9])?[\\h]?([AP][M])?)?\\b");
        // added outer \\b because it couldnt detect "3.55 - 4.15pm."
        //\b(2359|23:59)\b
        Matcher matcher = pattern.matcher(text1);

        //if its a single digit, with no 'at' or no pm/am then ditch it
        // if its 1-9 with no at/pm/am then ditch
        // if it has to/and/-/o'clock then keep it
        while (matcher.find()) {
            String secondParse = matcher.group(0);
            System.out.println(secondParse + " this includes timings as well as random numbers.");

            startHour = 99;
            startMinute = 99;
            endHour = 99;
            endMinute = 99;
            foundInloop = 0;

            if(secondParse.contains("$")) {
                System.out.println( secondParse + "$ detected, false parse.");
            }

            else {
                if (secondParse.contains("AM") || secondParse.contains("PM")) {
                    viableTimeCount++;
                    String thirdParse = secondParse;
                    System.out.println(thirdParse + " this should include am/pm ");
                    Pattern pattern2 = Pattern.compile("(2[0-3]|1[0-9]|0?[0-9])([:.][0-5][0-9])?");
                    //Pattern pattern2 = Pattern.compile("(1[0-2]|0?[1-9])([:.][0-5][0-9])?");
                    Matcher matcher2 = pattern2.matcher(thirdParse);
                    // just the numbers eg 7:30-9pm -> 7:30 , 9

                    boolean a = thirdParse.contains("AT");
                    // start timing only? or might this be ambiguous
                    boolean b = thirdParse.contains("-") || thirdParse.contains("AND") || thirdParse.contains("TO");
                    // means there's a range , start and end timing
                    boolean c = thirdParse.contains("BY");

                    // Splittime function checks if the minute portion is ==0, if it is it wont return a valid start/endminute
                    // right now start/endminute will be = 0 as initialized if they are useless 0s like 04:00, probably need to change this.
                    if (thirdParse.contains("AM") && thirdParse.contains("PM")) {
                        // thirdParse will be :eg 11am to 1pm... / 11pm to 1am
                        // means it is definitely a range, don't have to check for a / b / c
                        // we dont need to user matcher2, we just cut up thirdParse.
                        // check if AM or PM comes first.
                        Pattern pattern3 = Pattern.compile("(2[0-3]|1[0-9]|0?[0-9])([:.][0-5][0-9])?[\\h]?([AP][M])?");
                        // splits it into 11am, 1pm
                        Matcher matcher3 = pattern3.matcher(thirdParse);

                        System.out.println(thirdParse + " should be a range");

                        while (matcher3.find()) {
                            ArrayList<Integer> temp = splitTime(matcher3.group(0));
                            //System.out.println(" foundinloop = " + foundInloop );
                            if (foundInloop == 0) {
                                //start timings

                                startHour = temp.get(0);
                                System.out.println("start Hour set to " + startHour);

                                if (temp.size() == 2) {
                                    startMinute = temp.get(1);
                                    System.out.println("start minute set to " + startMinute);
                                }

                                foundInloop++;

                            } else if (foundInloop == 1) {
                                //end timings
                                endHour = temp.get(0);
                                if (temp.size() == 2) {
                                    endMinute = temp.get(1);
                                }
                                foundInloop = 0;


                            }


                        }
                    } else if (thirdParse.contains("AM")) {

                        if (b) {
                            while (matcher2.find()) {
                                System.out.println(thirdParse + " should be a range");
                                // find 2 groups, eg 7:30-9pm -> 7:30 , 9
                                ArrayList<Integer> temp = splitTime(matcher2.group(0));

                                if (foundInloop == 0) {
                                    startHour = temp.get(0);
                                    if(startHour == 12) {
                                        startHour = 0;
                                        //12AM to 0.00 (24hr clock)
                                    }
                                    if (temp.size() == 2) {
                                        startMinute = temp.get(1);
                                    }
                                    foundInloop++;
                                } else if (foundInloop == 1) {
                                    endHour = temp.get(0);
                                    if(endHour == 12) {
                                        endHour = 0;
                                        //12AM to 0.00 (24hr clock)
                                    }
                                    if (temp.size() == 2) {
                                        endMinute = temp.get(1);
                                    }
                                    foundInloop = 0;
                                }
                            }
                            // means time range, with start and end time. could include minutes....
                        } else if (c) {
                            // should only be 1 number since its eg. by 7
                            if (matcher2.find()) {
                                ArrayList<Integer> temp = splitTime(matcher2.group(0));
                                endHour = temp.get(0);
                                if(endHour == 12) {
                                    endHour = 0;
                                    //12AM to 0.00 (24hr clock)
                                }
                                if (temp.size() == 2) {
                                    endMinute = temp.get(1);
                                }
                            }
                            // end time only
                        } else if (a) {
                            // at 7 (am)
                            //start time only
                            if (matcher2.find()) {
                                ArrayList<Integer> temp = splitTime(matcher2.group(0));
                                startHour = temp.get(0);
                                if(startHour == 12) {
                                    startHour = 0;
                                    //12AM to 0.00 (24hr clock)
                                }
                                if (temp.size() == 2) {
                                    startMinute = temp.get(1);
                                }
                            }

                        } else {
                            // 04:00 AM , no at,by,or range indicator
                            // sets startime to 16:00 immediately
                            if (matcher2.find()) {
                                ArrayList<Integer> temp = splitTime(matcher2.group(0));
                                startHour = temp.get(0);
                                if(startHour == 12) {
                                    startHour = 0;
                                    //12AM to 0.00 (24hr clock)
                                }
                                if (temp.size() == 2) {
                                    startMinute = temp.get(1);
                                }
                            }
                        }
                    } else if (thirdParse.contains("PM")) {
                        if (b) {
                            System.out.println(thirdParse + " should be a range");
                            while (matcher2.find()) {
                                // find 2 groups, eg 7:30-9pm -> 7:30 , 9
                                ArrayList<Integer> temp = splitTime(matcher2.group(0));

                                if (foundInloop == 0) {
                                    startHour = temp.get(0);
                                    if (startHour < 12 && startHour != 0) {
                                        startHour = startHour + 12;
                                        // to fit 24hr clock
                                    }

                                    if (temp.size() == 2) {
                                        startMinute = temp.get(1);
                                    }
                                    foundInloop++;
                                } else if (foundInloop == 1) {
                                    endHour = temp.get(0);
                                    if (endHour < 12 && endHour != 0) {
                                        endHour = endHour + 12;
                                    }
                                    if (temp.size() == 2) {
                                        endMinute = temp.get(1);
                                    }
                                    foundInloop = 0;
                                }
                            }
                            // means time range, with start and end time. could include minutes....
                        } else if (c) {
                            // should only be 1 number since its eg. by 7
                            if (matcher2.find()) {
                                ArrayList<Integer> temp = splitTime(matcher2.group(0));
                                endHour = temp.get(0);
                                if (endHour < 12 && endHour != 0) {
                                    endHour = endHour + 12;
                                }

                                if (temp.size() == 2) {
                                    endMinute = temp.get(1);
                                }

                            }
                            // end time only
                        } else if (a) {
                            // at 7 (pm)
                            //start time only
                            if (matcher2.find()) {
                                ArrayList<Integer> temp = splitTime(matcher2.group(0));
                                startHour = temp.get(0);
                                if (startHour < 12 && startHour != 0) {
                                    startHour = startHour + 12;
                                }
                                if (temp.size() == 2) {
                                    startMinute = temp.get(1);
                                }
                            }
                        } else {
                            if (matcher2.find()) {
                                ArrayList<Integer> temp = splitTime(matcher2.group(0));
                                startHour = temp.get(0);
                                if (startHour < 12 && startHour != 0) {
                                    startHour = startHour + 12;
                                }
                                if (temp.size() == 2) {
                                    startMinute = temp.get(1);
                                }
                            }
                        }
                    } else {
                        //thirdparse doesnt have am/pm. eg At 4 or 4 o'clock
                        System.out.println("Ambiguous timing (no am/pm) but has at/oclock/by : " + thirdParse);
                    }

                    System.out.println("startHour is " + startHour + " startMinute is " + startMinute + " to " + " endHour is : " +
                            endHour + " endMinute is " + endMinute);
                } else if (secondParse.contains(":") || secondParse.contains(".")) {
                    // but no am/pm
                    // may be 24hr
                    // no at/by/range
                    String thirdParse = secondParse;
                    //eg 14:00 or 12:00
                    // should be 24hrclock
                    System.out.println(thirdParse + " this should only include : or . ");
                    Pattern pattern2 = Pattern.compile("(2[0-3]|1[0-9]|0?[0-9])([:.][0-5][0-9])?");
                    //from 23:59 it takes 2 out....
                    // can i remove the ? since it alr contains : or .??
                    // what if its 2.30 to 3
                    // examples 3:00 to 4:00, at 12:00, 2:30 to 3
                    Matcher matcher2 = pattern2.matcher(thirdParse);

                    boolean a = thirdParse.contains("AT");
                    // start timing only? or might this be ambiguous
                    boolean b = thirdParse.contains("-") || thirdParse.contains("AND") || thirdParse.contains("TO");
                    // means there's a range , start and end timing
                    boolean c = thirdParse.contains("BY");
                    // end timing only?

                    if (b) {
                        viableTimeCount++;
                        System.out.println(thirdParse + " should be a range");

                        while (matcher2.find()) {
                            // find 2 groups, eg 7:30-9pm -> 7:30 , 9
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));

                            if (foundInloop == 0) {
                                startHour = temp.get(0);

                                if (temp.size() == 2) {
                                    startMinute = temp.get(1);
                                }
                                foundInloop++;
                            } else if (foundInloop == 1) {
                                endHour = temp.get(0);

                                if (temp.size() == 2) {
                                    endMinute = temp.get(1);
                                }
                                foundInloop = 0;
                            }
                        }
                        // means time range, with start and end time. could include minutes....
                    } else if (c) {
                        viableTimeCount++;
                        // should only be 1 number since its eg. by 7
                        if (matcher2.find()) {
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));

                            endHour = temp.get(0);

                            if (temp.size() == 2) {
                                endMinute = temp.get(1);
                            }
                        }
                        // end time only
                    } else if (a) {
                        viableTimeCount++;
                        // at 7 (am)
                        //start time only
                        if (matcher2.find()) {
                            ArrayList<Integer> temp = splitTime(matcher2.group(0));
                            startHour = temp.get(0);
                            if (temp.size() == 2) {
                                startMinute = temp.get(1);
                            }
                        }
                    } else {
                        // no at/by/and/to/-
                        System.out.println(thirdParse + " has no pm/am but has :/. uncertain whether it is a time or not");
                    }
                    System.out.println("startHour is " + startHour + " startMinute is " + startMinute + " to " + " endHour is : " +
                            endHour + " endMinute is " + endMinute);
                } else if (secondParse.contains("O'CLOCK")) {
                    //viableTimeCount++;
                    System.out.println(secondParse + " contains o'clock, ambiguous");

                } else if (secondParse.contains("AT") || secondParse.contains("BY")) {
                    //viableTimeCount++;
                    System.out.println(secondParse + " contains AT or BY but no AM/PM, ambiguous");
                } else {
                    System.out.println(secondParse + " is a false parse.");
                }
            }
        }

        if (viableTimeCount > 1 || viableTimeCount == 0) {
            if (viableTimeCount > 1) {
                System.out.println("There are more than 1 viable times in this string.");
                Time.add(586);
                Time.add(586);
                Time.add(586);
                Time.add(586);
            } else {
                System.out.println("NO VIABLE TIME");
                Time.add(99);
                Time.add(99);
                Time.add(99);
                Time.add(99);
            }
            //default
        } else if (viableTimeCount == 1) {
            Time.add(startHour);
            Time.add(startMinute);
            Time.add(endHour);
            Time.add(endMinute);
            System.out.println("Successfully saved to Time, with only 1 viable time");
        }
        return Time;
    }


    public String EventParse(String emailBody) {
        String eventType = new String();

        Pattern pattern = Pattern.compile("(QUIZ|TEST|EXAM)");
        // incomplete, may need add more stuff
        Matcher matcher = pattern.matcher(emailBody.toUpperCase());

        while (matcher.find()) {
            eventType = matcher.group(0);
        }

        return eventType;
    }

    public ArrayList<Integer> splitTime(String time) {
        //time here will be just integers like 07:40 or 7 or 12:30
        ArrayList<Integer> timeStorage = new ArrayList<>();
        Pattern pattern = Pattern.compile("(2[0-3]|1[0-9]|0?[0-9])");
        Pattern pattern1 = Pattern.compile("[:.][0-5][0-9]");
        Matcher matcher = pattern.matcher(time);
        Matcher matcher1 = pattern1.matcher(time);
        System.out.println("In splitTime, : " + time);
        int hour = 0;
        int minute = 0;
        int flag = 0;

        if (time.startsWith("13") || time.startsWith("14") || time.startsWith("15") || time.startsWith("16") || time.startsWith("17") || time.startsWith("18") ||
                time.startsWith("19") || time.startsWith("20") || time.startsWith("21") || time.startsWith("22") || time.startsWith("23") || time.startsWith("00")) {
            System.out.println(time + " in splitTime 24hr section");

            while (matcher.find() && matcher1.find()) {
                flag++;

                // has to be && because matcher might find the minute time.

                hour = Integer.parseInt(matcher.group(0));
                // already PM time because its in 24hr and we only accept hour as 24hr clock time+ pm


                minute = Integer.parseInt(matcher1.group(0).substring(1));
                // to skip : or .


                timeStorage.add(hour);

                if (minute < 60) {
                    timeStorage.add(minute);
                }

            }
            if(flag == 0) {
                hour = 99;
                minute = 99;
                timeStorage.add(hour);
                timeStorage.add(minute);
            }
        } else {
            if (time.contains(".") || time.contains(":")) {
                // the pm am is already considered in TimeParse code i think
                System.out.println(time + " contains : or .");

                while (matcher.find() && matcher1.find()) {
                    //should this be ||?
                    //System.out.println(matcher.group(0));
                    if (matcher.group(0).startsWith("0")) {
                        hour = Integer.parseInt(matcher.group(0).substring(1));

                    } else {
                        hour = Integer.parseInt(matcher.group(0));
                        if (time.contains("PM") && hour < 12 && hour != 0) {
                            hour = hour + 12;
                        }
                        if(time.contains("AM") && hour == 12) {
                            hour = 0;
                        }
                    }
                    //System.out.println(hour);

                    minute = Integer.parseInt(matcher1.group(0).substring(1));
                    // to skip : or .

                    timeStorage.add(hour);

                    if (minute < 60) {
                        timeStorage.add(minute);
                    }
                }
            } else if (time.contains("PM") || time.contains("AM")) {
                // but no . or :
                while (matcher.find()) {
                    if (time.contains("PM")) {
                        if (matcher.group(0).startsWith("0")) {
                            hour = Integer.parseInt(matcher.group(0).substring(1));
                        } else {
                            hour = Integer.parseInt(matcher.group(0));
                            if ( hour < 12 && hour != 0) {
                                hour = hour + 12;
                            }
                        }
                    } else if (time.contains("AM")) {
                        if (matcher.group(0).startsWith("0")) {
                            hour = Integer.parseInt(matcher.group(0).substring(1));
                        } else {
                            hour = Integer.parseInt(matcher.group(0));
                            if(hour == 12) {
                                hour = 0;
                                //12AM to 0.00 (24hr clock)
                            }
                        }
                    }
                    timeStorage.add(hour);
                }
            } else {
                // just hour, no minutes, no am/pm
                // idk if this even happens..
                if (time.startsWith("0")) {
                    timeStorage.add(Integer.parseInt(time.substring(1)));

                } else {
                    timeStorage.add(Integer.parseInt(time));

                }
                timeStorage.add(0);
            }
        }

        System.out.println(timeStorage.get(0) + " at end of split time");

        return timeStorage;
    }
}

