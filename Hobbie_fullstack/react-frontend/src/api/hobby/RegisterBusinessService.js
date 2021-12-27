import React from 'react'
import axios from 'axios';

const RegisterBusinessService = (user) => {
    let axiosConfig = {
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            "Access-Control-Allow-Origin": "*",
        }
      };

    return (
   
        axios.post(`http://localhost:8080/users//register-business`, user, axiosConfig)
             .then(res => {
                 if(res.data != null){
                  // () => res.redirect('/user-home')
                    return res;
                 }
               }) .catch(err => {
                 return err.response;
               }));
}

export default RegisterBusinessService
