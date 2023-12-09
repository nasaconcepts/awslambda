package org.example.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class i18n {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en"));
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
       String sayHello =  bundle.getString("greeting");
        System.out.println(sayHello);
        Locale.setDefault(new Locale("fr"));
        bundle = ResourceBundle.getBundle("messages");
        sayHello =  bundle.getString("greeting");
        System.out.println(sayHello);

    }
}
