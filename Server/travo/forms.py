#-*- coding: utf-8 -*-
from django import forms

class RegisterForm(forms.Form):
    nickname = forms.CharField(max_length=100)
    email = forms.EmailField()
    password = forms.CharField()


    def send_email(self):
        print "Trying to send a validation email to user."
        

