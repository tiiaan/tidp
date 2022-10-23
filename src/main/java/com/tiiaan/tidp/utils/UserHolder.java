package com.tiiaan.tidp.utils;

import com.tiiaan.tidp.dto.UserDTO;

public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    //private static final ThreadLocal<UserDTO> tl = ThreadLocal.withInitial(UserDTO::new);

    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    public static UserDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
