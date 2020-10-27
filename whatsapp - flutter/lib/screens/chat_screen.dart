
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:whatsapp/Message.dart';
import 'package:whatsapp/screens/message_enry.dart';
import 'package:sqflite/sqflite.dart';
import 'package:cloud_firestore/cloud_firestore.dart';

import '../DB.dart';
class chat_screen extends StatefulWidget {
  @override
  _chat_screenState createState() => _chat_screenState();
}

class _chat_screenState extends State<chat_screen> {



  List <Message> messages = [];

  bool first_time ;
  String my_number;
  String ConversationNumber;
Stream collectionStream;
  String table_name;



  Map data_recieverd_from_home_screen = {};
  final controler = TextEditingController();
  final ScrollController my_controller = new ScrollController();

  @override
  void dispose() {
    super.dispose();
    controler.dispose();
  }

  @override
  void initState() {
    super.initState();
    first_time = true ;
    WidgetsBinding.instance
        .addPostFrameCallback((_) => control());
  }

  void one_time_at_the_begin()async
  {
    //get_all_messages(table_name);
    messages = await DB.get_all_messages(table_name);
    firebase();
    first_time =false;
    setState(() {});
  }




void firebase()  {
  collectionStream = FirebaseFirestore.instance.collection(
      'users').doc(my_number).collection("messages").snapshots();
  collectionStream.listen((event) {
    QuerySnapshot querySnapshot = event;
    String source = querySnapshot != null && querySnapshot.metadata.hasPendingWrites
        ? "Local" : "Server";
    querySnapshot.docChanges.forEach((documentchange)  async {
          if(source == "Server" && documentchange.type == DocumentChangeType.added  )
            {
              String conversationNumber = documentchange.doc.data()['conversationNumber'];
              String msg = documentchange.doc.data()['msg'];
              DateTime Time = documentchange.doc.data()['sentTime'].toDate();
              Timestamp seen = documentchange.doc.data()['seen'];
              if (seen == null)
                {

                  await  FirebaseFirestore.instance.collection(
                      'users').doc(my_number).collection("messages").doc(
                      documentchange.doc.id).update(
                      {'seen': FieldValue.serverTimestamp()});

                  await FirebaseFirestore.instance.collection(
                      'users').doc(my_number).collection("messages").doc(
                      documentchange.doc.id).update({'recieved': true});

                  Message message = new Message(content: msg,
                      Date: DateFormat.jm().format(Time),
                      MessageWriter: conversationNumber);
                  await DB.Insert_message_to_table(message, table_name);
                  await DB.update_conversation_last_message(message,conversationNumber);
                  messages = await DB.get_all_messages(table_name);
                  setState(() {});
                  print(msg);

                }
            }

          control();

    });

  },onError: (e)
  {print("hhhhhhhhhhhhhhhhhhhhhhhhh  $e");}
  );

}


control()
{
  my_controller.animateTo((10000000000000),
      duration: const Duration(milliseconds: 100),
      curve: Curves.easeOut);
}



  @override
  Widget build(BuildContext context) {
    data_recieverd_from_home_screen = ModalRoute
        .of(context)
        .settings
        .arguments;
    my_number = data_recieverd_from_home_screen['my_number'];
    ConversationNumber = data_recieverd_from_home_screen['Conversation_Number'];
    table_name = "T" + ConversationNumber;

    if (first_time == true )
      {
        one_time_at_the_begin();
      }


    Future<void>  send_message() async {
      if (controler.text == "") {
        return null;
      }
      else {
        Message message = new Message(MessageWriter: "me",
            content: controler.text,
            Date:  DateFormat.jm().format(DateTime.now()));
        await DB.Insert_message_to_table(message, table_name);
        await DB.update_conversation_last_message(message, ConversationNumber);

        return FirebaseFirestore.instance.collection('users')
            .doc(ConversationNumber)
            .collection("messages")
            .add({
          "msg": controler.text,
          "sentTime": FieldValue.serverTimestamp(),
          "conversationNumber": my_number
        })
            .then((value) async {
          controler.clear();
          messages = await DB.get_all_messages(table_name);
          control();
          setState(() {});
        })
            .catchError((error) => print("Failed to add user: $error"));
      }
    }



    return Scaffold(

      appBar: AppBar(

        leading: CircleAvatar(
          backgroundColor: Colors.white,
          radius: 10.0,
        ),
        title: Text(data_recieverd_from_home_screen['Conversation_Number']),
        backgroundColor: Color(0xff065d54),
      ),
      body: Column(
        children: [
          Expanded(
            flex: 12,
            child: ListView.builder(
                controller: my_controller,
                itemCount: messages.length,
                itemBuilder: (context, indix) {
                  return message_entry(content: messages[indix].content,
                    MessageWriter: messages[indix].MessageWriter,
                    date: messages[indix].Date,);
                }
            ),
          ),
          Expanded(
            flex: 2,
            child: Row(
              children: [
                Expanded(
                  flex: 5,
                  child: TextField(
                    controller: controler,
                    obscureText: false,
                    decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      labelText: 'message',
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: CircleAvatar(
                    backgroundColor: Color(0xff065d54),
                    child: FlatButton(
                      onPressed:send_message,

                      child: Icon(
                        Icons.send,
                        color: Colors.white,
                      ),
                    ),
                  ),
                )
              ],
            ),
          ),
        ],
      ),

    );
  }

}