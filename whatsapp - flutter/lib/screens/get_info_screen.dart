import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class get_info extends StatefulWidget {
  @override
  _get_infoState createState() => _get_infoState();
}


class _get_infoState extends State<get_info> {

  void store_the_number(String my_number) async
  {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString("my_number", my_number);
    controler.dispose();
    Navigator.pushReplacementNamed(context, '/');
  }

  final controler = TextEditingController();
  @override
  void dispose() {
    super.dispose();
    controler.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body:  Column(
        mainAxisSize:MainAxisSize.min ,
        children: [
          Expanded(child: SizedBox(height: 80,)),
          Expanded(child:Text ("Enter your phone number",
            style: TextStyle(
              color: Color(0xff128c7e),
              fontSize: 18,
            ),
          )),
          Expanded(child: SizedBox(height: 80,)),
          Expanded(
            child: TextField(
              keyboardType: TextInputType.number,
              obscureText: false,
              controller: controler,
              decoration: InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'phone number',
                hintText: 'phone number'
              ),
            ),
          ),
          Expanded(child: SizedBox(height: 200,)),
          RaisedButton(
            child: Text ("NEXT"),
            onPressed: (){
              store_the_number(controler.text);
            },
            color: Color(0xff00cc3f)
          ),
          Expanded(child: SizedBox(height: 200,)),


        ],
      ),
    )
    ;



  }
}
