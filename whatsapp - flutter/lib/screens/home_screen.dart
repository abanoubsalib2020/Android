
import 'dart:async';

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:whatsapp/Conversation.dart';
import 'package:whatsapp/Message.dart';

import '../DB.dart';


class home_screen extends StatefulWidget {

  @override
  _home_screenState createState() => _home_screenState();
}

class _home_screenState extends State<home_screen> {
  String mynumber;
  StreamSubscription reference;
  Stream collectionStream;
  bool first_time ;

  List <Conversation> Conversations = [];

  Conversation IsConversationExist(String ConversationNumber) {
    for (int i = 0; i < Conversations.length; i++) {
      if (ConversationNumber == Conversations[i].ConversationNumber) {
        return Conversations[i];
      }
    }
    return null;
  }
  void one_time_at_the_begin ()async
  {

    Conversations = await DB.get_all_Conversation();
    firebase();
    first_time = false;
    setState(() {
    });
  }


  void firebase()  {
    collectionStream = FirebaseFirestore.instance.collection(
        'users').doc(mynumber).collection("messages").snapshots();
    collectionStream.listen((event) {
      QuerySnapshot querySnapshot = event;
      String source = querySnapshot != null && querySnapshot.metadata.hasPendingWrites
          ? "Local" : "Server";

      querySnapshot.docChanges.forEach((documentchange) async   {
        if(source == "Server" && documentchange.type == DocumentChangeType.added  ) {
          String conversationNumber = documentchange.doc
              .data()['conversationNumber'];
          String msg = documentchange.doc.data()['msg'];
          DateTime Time = documentchange.doc.data()['sentTime'].toDate();
          bool recieved = documentchange.doc.data()['recieved'];

          if ((recieved == null || recieved != true)) {
            await FirebaseFirestore.instance.collection(
                'users').doc(mynumber).collection("messages").doc(
                documentchange.doc.id).update({'recieved': true});

            String time = DateFormat.jm().format(Time);
            Message message = new Message(content: msg,
                Date: time,
                MessageWriter: conversationNumber);


            Conversation current = IsConversationExist(conversationNumber);
            if (current == null) {
              print("Null");
              current = new Conversation(ConversationNumber: conversationNumber,
                  LastMessage: msg,
                  Time: time);
              await DB.create_Conversation(current);
              await DB.create_message_table(conversationNumber);
              //await get_all_Conversation();
              Conversations = await DB.get_all_Conversation();
              setState(() {});
            }
            else {
              print("Else");
              await DB.update_conversation_last_message(message,conversationNumber);
              Conversations = await DB.get_all_Conversation();
              setState(() {});
            }
          }
        }});

    },onError: (e)
    {print("hhhhhhhhhhhhhhhhhhhhhhhhh  $e");}
    );

  }






  @override
  void initState() {
    super.initState();

    first_time = true;

  }

  @override
  Widget build(BuildContext context) {

    Map data = ModalRoute
        .of(context)
        .settings
        .arguments;
    mynumber = data ['my_number'];
    if (first_time == true )
    {
      one_time_at_the_begin();

    }



    return DefaultTabController(
      length: 4,
      child: Scaffold(
          floatingActionButton: FloatingActionButton(
            onPressed: () async{
              first_time = true;
              await Navigator.pushNamed(context, '/add_contact',arguments: {"mynumber":mynumber});
              setState(() {});
            },
            backgroundColor: Colors.green,
            child: Icon(
              Icons.message,
            ),
          ),
          appBar: AppBar(
            bottom: TabBar(
              indicatorColor: Colors.white,
              indicatorWeight: 3,
              tabs: [
                Tab(icon: Icon(Icons.camera_alt),),
                Tab(child: Text(
                  "CHATS",
                ),
                ),
                Tab(text: "STATUS",),
                Tab(text: "CALLS",)
              ],
            ),
            backgroundColor: Color(0xff065d54),
            title: Text("WhatsApp"),
          ), body: TabBarView(
          children: [
            Center(child: Text("camera")),
            ListView.builder(
                itemCount: Conversations.length,
                itemBuilder: (context, indix) {
                  return Card(
                      margin: EdgeInsets.fromLTRB(5, 5, 5, 0),
                      child: ListTile(
                        onTap: () async {
                          first_time = true;
                          print("hi ${Conversations[indix].ConversationName}");
                          await Navigator.pushNamed(context, '/chat', arguments:
                          {
                            'Conversation_Number': Conversations[indix]
                                .ConversationNumber,
                            'my_number': mynumber
                          }
                          );
                          setState(() {});
                        },
                        onLongPress: () {},
                        leading: CircleAvatar(
                          backgroundColor: Colors.grey,
                        ),
                        trailing: Text(Conversations[indix].Time),
                        title: Text(Conversations[indix].ConversationName),
                        subtitle: Text(Conversations[indix].LastMessage),
                      ));
                }),
            Center(child: Text('BIRDS')),
            Center(child: Text('BIRDS'))
          ]
      )


      ),
    );
  }

  }




