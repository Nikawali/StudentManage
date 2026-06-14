package org.example.utils;

public class LoginResult<T>{


        private String token;

        private String role;

        private T data;

        public LoginResult(){}

        public LoginResult(String token,String role,T data){
            this.token = token;
            this.role = role;
            this.data=data;
        }

       public void setToken(String token){
            this.token=token;
       }

       public String getToken(){
            return token;
       }

       public void setRole(String role){
            this.role=role;
       }

       public String getRole(){
            return role;
       }

       public void setData(T data){this.data=data;}

       public T getData(){return data;}
}
