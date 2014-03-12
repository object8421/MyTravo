#-*- coding: utf-8 -*-
from django import forms

class RegisterForm(forms.Form):
    nickname = forms.CharField(max_length=100)
    password = forms.CharField()
    email = forms.EmailField()
    user_type = forms.CharField(required = False)

    def send_email(self):
        print "Trying to send a validation email to user."
        

