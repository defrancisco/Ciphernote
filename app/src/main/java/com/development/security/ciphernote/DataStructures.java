package com.development.security.ciphernote;

import java.util.Date;

/**
 * Created by danie on 1/19/2018.
 */

public class DataStructures {
    static class UserConfiguration{
        public UserConfiguration(){

        }
        private String passwordHash;
        private int iterations;
        private String salt;
        public void setPasswordHash(String value){
            passwordHash = value;
        }
        public String getPasswordHash(){
            return passwordHash;
        }
        public void setIterations(int value){
            iterations = value;
        }
        public int getIterations(){
            return iterations;
        }
        public void setSalt(String value){
            salt = value;
        }
        public String getSalt(){
            return salt;
        }

    }

    static class FileManagmentObject{
        public FileManagmentObject(){

        }
        public String userDefinedFileName;
        public Date lastAccessed;
    }

    class notePage {

    }
}