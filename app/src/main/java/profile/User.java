package profile;

public class User {

    public String username,password,displayName,fullName,birthDay,phoneNum,height,weight,homeAddress;;


    public User(){}


    public User(String username, String password){
        this.password = password;
        this.username = username;
    }


    public User(String displayName, String fullName, String birthDay, String phoneNum, String height, String weight, String homeAddress) {
        this.displayName = displayName;
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.phoneNum = phoneNum;
        this.height = height;
        this.weight = weight;
        this.homeAddress = homeAddress;
    }


}
