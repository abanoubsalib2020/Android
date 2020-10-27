import 'package:whatsapp/Conversation.dart';
import 'package:whatsapp/screens/add_contact.dart';

import 'screens/chat_screen.dart';
import 'screens/first_screen.dart';
import 'screens/get_info_screen.dart';
import 'screens/home_screen.dart';

import 'package:firebase_core/firebase_core.dart';

import 'package:flutter/material.dart';
import 'package:sqflite/sqflite.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  runApp(MaterialApp(
   routes: {
      '/' : (context) { return first_screen() ; },
      '/home': (context) { return home_screen() ;},
      '/chat':(context) { return chat_screen() ;},
      '/get_info' :(context) {return get_info() ; },
     '/add_contact' :(context) {return add_contact() ; }
     },

  ));
}


