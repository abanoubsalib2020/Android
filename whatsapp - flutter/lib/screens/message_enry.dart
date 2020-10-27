import 'package:flutter/material.dart';
import 'dart:math';
class message_entry extends StatelessWidget {
  String content;
  String  MessageWriter; // me if i the sender
  String date ;

  message_entry({this.content, this.MessageWriter,this.date});

  @override
  Widget build(BuildContext context) {
    dynamic sender = (MessageWriter == "me") ? MainAxisAlignment.end : MainAxisAlignment.start  ;
    dynamic the_color = (MessageWriter == "me") ?   Color(0xffe2ffc7) : Colors.white ;
    int number_of_lines =   (content.length  / 30).ceil() ;
    List <String> temp = [];
    while (true)
      {
        if (content.length >= 30)
          {
            temp.add(content.substring(0,29));
          }else
            {
              temp.add(content.substring(0));
              break;
            }
        content = content.substring(30);
      }
    print(temp);

    return Row(
        mainAxisAlignment: sender,
        children: [
          Container(
              margin: EdgeInsets.fromLTRB(10, 10, 10, 0),
              child: Container(
                  child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        Card(
                          color: the_color,
                          child:Column(
                            crossAxisAlignment: CrossAxisAlignment.end,
                            children: [
                              Column(
                                crossAxisAlignment:  CrossAxisAlignment.start,
                                children: temp.map((e)
                                {
                                  return Text(e);
                                }).toList(),
                              ),
                              Card(
                                color: the_color,
                                child: Text(date),
                              )
                            ],
                          ),

                        )



                      ]
                  )
              )
          )
        ]
    );
  }
}