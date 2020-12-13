package be.alexandre01.shop.calendar;

import be.alexandre01.shop.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class TimersManager extends BukkitRunnable {
    public static ArrayList<DateContainer> index = new ArrayList<>();

    private String message;
    public TimersManager(String message){
      this.message = message;
      init();
    }
    public void init(){
        System.out.println("init");
        java.util.Date nowDay = new java.util.Date();
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        for (DateContainer date : index){


            if(date.getDay().equalsIgnoreCase(format.format(nowDay))){
                Date now = new Date();

                if(date.getHour()> now.getHours() ||  date.getMinute()>= now.getMinutes() && date.getHour() >= now.getHours()){

                    int hour =  date.getHour() - now.getHours();
                    int minute = date.getMinute() - now.getMinutes();
                    int seconds = now.getSeconds()+1;

                /*    System.out.println("sec"+seconds);
                    System.out.println(hour);
                    System.out.println(minute);
                    System.out.println(((20*60*60)*hour)+((20*60)*minute)-((20)*seconds));*/
                    runTaskLaterAsynchronously(Main.instance,((20*60*60)*hour)+((20*60)*minute)-((20)*seconds));
                    return;
                }
            }
        }
        boolean hasFound = false;
        java.util.Date afterDay = new java.util.Date();
        int day = 0;
        while (!hasFound){
            Calendar c = Calendar.getInstance();
            c.setTime(afterDay);
            c.add(Calendar.DATE, 1);
            afterDay = c.getTime();

            for (DateContainer date : index){
                if(date.getDay().equalsIgnoreCase(format.format(afterDay))){
                    hasFound = true;
                    return;
                }
            }
            day++;
        }
        Main.instance.getLogger().severe("Error");
    }

    @Override
    public void run() {
        this.cancel();
        Main.instance.getEventSell().clear();
        Main.instance.getSell_amount().clear();
        Main.instance.getLastShopItem().clear();
        Bukkit.broadcastMessage(message);
        Bukkit.getScheduler().runTaskLater(Main.instance, new BukkitRunnable() {
            @Override
            public void run() {
                new TimersManager(message);
            }
        }, 20L * 61L);
    }
}
