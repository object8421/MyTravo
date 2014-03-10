from django.shortcuts import render
from django.contrib.auth.models import User
from django.template import RequestContext, loader
from django.http import HttpResponse

def index(request):
    #return HttpResponse("Hello, world. You're at the travo index.")
    template = loader.get_template('travo/test.html')
    context = RequestContext(request, )
    return HttpResponse(template.render(context))
def home_page(request):
    template = loader.get_template('travo/homepage.html')
    context = RequestContext(request, )
    return HttpResponse(template.render(context))
def create_user_view(request):
    user = User.objects.create_user(request.username, request.email, \
    	request.password)
    user.save()

def login_view(request):
    user = authenticate(username=request.username, password=request.password)
    if user is not None:
        # the password verified for the user
        if user.is_active:
            print("User is valid, active and authenticated")
        else:
            print("The password is valid, but the account has been disabled!")
    else:
        # the authentication system was unable to verify the username and password
        print("The username and password were incorrect.")

