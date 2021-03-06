package Reminder;

import java.security.spec.DSAPrivateKeySpec;
import java.util.LinkedList;
import java.util.List;

public class Reminder implements Comparable<Reminder> {


    private String medicineName;
    private int hour;
    private int minute;
   // private boolean daysOfWeek[] = new boolean[7];
    private String daysOfWeek;
    private String dosageUnit;
    private String dosageQuantity;
    private String instructions;
    private String repeatTime;
    private String nodeKey;
    //private int medicineId;




    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
/*

    public boolean[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(boolean[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
*/

    public String getDosageUnit() {
        return dosageUnit;
    }

    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    public String getDosageQuantity() {
        return dosageQuantity;
    }

    public void setDosageQuantity(String dosageQuantity) {
        this.dosageQuantity = dosageQuantity;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(String repeatTime) {
        this.repeatTime = repeatTime;
    }

   /* public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }*/

    public String getAmPm() { return (hour < 12) ? "am" : "pm"; }

    public Reminder(){
    }

    @Override
    public int compareTo(Reminder reminder) {
        if (hour < reminder.getHour())
            return -1;
        else if (hour > reminder.getHour())
            return 1;
        else {
            if (minute < reminder.getMinute())
                return -1;
            else if (minute > reminder.getMinute())
                return 1;
            else
                return 0;
        }
    }
}
