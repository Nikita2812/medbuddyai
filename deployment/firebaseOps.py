import firebase_admin
import os
import json
from firebase_admin import db
from dotenv import load_dotenv

class firebaseOps:
    def __init__(self):
        load_dotenv()
        cred= json.loads(os.environ["FIREBASE_CREDENTIALS"])
        cred1= firebase_admin.credentials.Certificate(cred)
        if not firebase_admin._apps:
            app=firebase_admin.initialize_app(cred1, {"databaseURL":"https://medbuddy-ai-default-rtdb.asia-southeast1.firebasedatabase.app/"})

    def get_username(self, name: str):
        ref= db.reference("users")
        list= ref.get()
        if list is None:
            return {
            "firstName": None,
            "lastName": None,
            "username": None
            }

        for user in list:
            if name==list[user]['username']:
                return list[user]
            
        return {
            "firstName": None,
            "lastName": None,
            "username": None
            }
    