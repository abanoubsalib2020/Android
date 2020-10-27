import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:whatsapp/DB.dart';

import '../Conversation.dart';

class add_contact extends StatefulWidget {
  @override
  _add_contactState createState() => _add_contactState();
}

class _add_contactState extends State<add_contact> {
  final user_name_controler = TextEditingController();
  final number_controler = TextEditingController();
  String mynumber ;
  List <Conversation> Conversations = [];
  @override
  Widget build(BuildContext context) {
    Map data = ModalRoute
        .of(context)
        .settings
        .arguments;
    mynumber = data['mynumber'];



    return Scaffold(
      appBar: AppBar(
        backgroundColor:  Color(0xff065d54),
        title: Text("Select contact"),
      ),
      body: Container(
        child: Column(
          children: [
            Card(
                margin: EdgeInsets.fromLTRB(5, 5, 5, 0),
                child: ListTile(
                  onTap: () {
                    showDialog<void>(context: context, builder: (context)
                    {
                     return AlertDialog(
                        title: Text('Adding New Contact'),
                        content: Container(
                          child: Column(
                            mainAxisSize:MainAxisSize.min ,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              TextField(
                            controller: user_name_controler,
                            obscureText: false,
                            decoration: InputDecoration(
                              border: OutlineInputBorder(),
                              labelText: 'Contact Name',
                            )),
                            TextField(
                              keyboardType: TextInputType.number,
                            controller: number_controler,
                            obscureText: false,
                            decoration: InputDecoration(
                            border: OutlineInputBorder(),
                            labelText: 'Contact Number',
                            )),
                            ],
                          ),
                        ),
                        actions: [
                          FlatButton(
                            textColor: Color(0xFF6200EE),
                            onPressed: () {Navigator.pop(context);},
                            child: Text('CANCEL'),
                          ),
                          FlatButton(
                            textColor: Color(0xFF6200EE),
                            onPressed: () async  {
                              Conversation conversation = new Conversation(ConversationNumber: number_controler.text.toString(),LastMessage: "",Time: "");
                              conversation.ConversationName = user_name_controler.text.toString();
                              Conversations.add(conversation);
                              await DB.create_Conversation(conversation);
                              await DB.create_message_table(number_controler.text.toString());

                              setState(() {

                              });
                              Navigator.pop(context);
                            },
                            child: Text('ADD'),
                          ),
                        ],
                      );
                    }
                    );

                  },
                  leading:CircleAvatar(
                    backgroundColor:Colors.green,
                    child:   Icon(Icons.person_add),
                  ),
                  title: Text("New contact"),
                )),
            Column (
              children: Conversations.map((conversation)
              {
                return Card(
                    margin: EdgeInsets.fromLTRB(5, 5, 5, 0),
                    child: ListTile(
                      onTap: () {
                        //reference.cancel();
                        Navigator.pushReplacementNamed(context, '/chat', arguments:
                        {
                          'Conversation_Number': conversation.ConversationNumber,
                          'my_number': mynumber
                        }
                        );
                      },
                      onLongPress: () {},
                      leading: CircleAvatar(
                        backgroundColor: Colors.grey,
                      ),
                      title: Text(conversation.ConversationName),
                    ));
              }).toList()

            )

          ],
        ),
      ),
    );
  }
}
