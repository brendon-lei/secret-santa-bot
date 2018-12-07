import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


public class Santa {


    public static void main (String []args) throws FileNotFoundException {
        ArrayList<Person> name = new ArrayList<>();
        ArrayList<Person> possibles = new ArrayList<>();
        String csvLine;
        Scanner in = new Scanner(new File("Secret Santa (Responses) - Responses 2.csv"));


        System.out.println("Add Names(enter \"Exit\" to exit): ");
        while(in.hasNextLine()){
            csvLine = in.nextLine();
            Person santa = personAsList(csvLine);
            name.add(santa);
            possibles.add(santa);

        }

        matchGenerator(name, possibles);

        // Email setup
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "805");

        // Lambdas are cool I guess
        Session session = Session.getDefaultInstance(props,new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Fill in your info here
                return new PasswordAuthentication("email@gmail.com","password");
            }
        });

        try {

            // Start a message object
            MimeMessage message = new MimeMessage(session);

            // From email
            message.setFrom(new InternetAddress("email@gmail.com"));

            for (int i = 0; i < name.size(); i++) {
                String to = name.get(i).getEmail();
                String messageBody = name.get(i).emailBody(name.get(i).getMatched());
                message.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(to));
                message.setSubject("Your secret santa pick is here!");
                message.setText(messageBody);
                Transport.send(message);
                System.out.println("Sent secret santa to " + name.get(i).getName());
            }
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

    static void matchGenerator(List<Person> names, List<Person> possibleMatches) {
        Collections.shuffle(possibleMatches);
        for(int i=0; i  < possibleMatches.size(); i++){
            if(possibleMatches.get(i).getName().equals(names.get(i).getName())){
                Collections.shuffle(possibleMatches);
            }
        }

        for (int i = 0; i < names.size(); i++) {
            // For testing purposes, this is commented it out to keep it completely blind
            // System.out.println(names.get(i).getName() + " -> " + possibleMatches.get(i).getName());
            names.get(i).addMatch(possibleMatches.get(i));
        }
    }

    static class Person {
        String name;
        String email;
        List<String> interests;
        Person matched;

        Person(String first, String mailing, String personInterest1, String personInterest2, String personInterest3 ) {
            this.name = first;
            this.email = mailing;
            this.interests = new ArrayList<>();
            this.interests.add(personInterest1);
            this.interests.add(personInterest2);
            this.interests.add(personInterest3);
        }

        // Allows for variable list sizes for interest lists
        Person(List<String> personList) {
            this.name = personList.get(0);
            this.email = personList.get(1);
            this.interests = new ArrayList<>();
            this.interests.addAll(personList.subList(2, personList.size()));
        }

        Person addMatch(Person match) {
            return this.matched = match;
        }

        Person getMatched() { return matched; }

        String getName(){
            return name;
        }

        String getEmail(){
            return email;
        }

        void getInterests(){
            for (String i : interests) {
                System.out.println(i);
            }
        }

        String emailBody(Person personalizedMatch) {
            StringBuilder baseBody = new StringBuilder();

            baseBody.append("You've been matched to " + personalizedMatch.name + ", here are some of their interests! \n");

            for (String item : personalizedMatch.interests) {
                baseBody.append(item + "\n");
            }

            return baseBody.toString();
        }
    }

    // Hardcoded for test data
    public static Person parseCSV(String str) {

        String[] santaSplit;

        String delim = ",";
        santaSplit = str.split(delim);

        Person santa = new Person(santaSplit[0], santaSplit[1], santaSplit[2], santaSplit[3], santaSplit[4]);

        for (int i = 0; i < str.length(); i++) {

        }
        return santa;
    }

    public static Person personAsList(String str) {
        boolean validString = false;
        StringBuilder sb = new StringBuilder();
        List<String> res = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {
            if (validString) {
                if (str.charAt(i) == '\"') {
                    if (i < str.length() - 1 && str.charAt(i + 1) == '\"') {
                        sb.append('\"');
                        i++;
                    }
                    else {
                        validString = false;
                    }
                }
                else {
                    sb.append(str.charAt(i));
                }
            }
            else {
                if (str.charAt(i) == '\"') {
                    validString = true;
                }
                else if (str.charAt(i) == ',') {
                    res.add(sb.toString());
                    sb.setLength(0);
                }
                else {
                    sb.append(str.charAt(i));
                }
            }
        }
        if (sb.length() > 0) {
            res.add(sb.toString());
        }

        Person santa = new Person(res);

        return santa;
    }
}

