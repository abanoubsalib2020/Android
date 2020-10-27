import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';



class first_screen extends StatefulWidget {
  @override
  _first_screenState createState() => _first_screenState();
}

class _first_screenState extends State<first_screen> {
  void get_my_information() async
  {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String my_number = prefs.getString("my_number");
    print ("hhhh $my_number");
    if (my_number == null) {
      Navigator.pushReplacementNamed(context, '/get_info');
    }
    else {
      print(my_number);
      Navigator.pushReplacementNamed(context, '/home', arguments:
      {
        'my_number': my_number
      }
      );
    }
  }

  @override
  void initState() {
    super.initState();
    get_my_information();
  }

  @override
  Widget build(BuildContext context) {
        return Scaffold(
          body:  Center(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  Expanded(flex:0,child: SizedBox(height: 200,)),
                  Expanded(
                    flex: 0,
                    child: Text("Whats app",
                    style: TextStyle(
                      color: Colors.green,
                      fontSize: 20,
                      fontWeight: FontWeight.bold,

                    ),
                    ),
                  ),
                  Expanded(flex:0,child: SizedBox(height: 250,)),
                  Expanded(
                      flex: 0,
                      child: Text("From ")),
                  Expanded(
                    flex: 0,
                    child: SizedBox(height: 10,),
                  ),
                  Expanded(
                    flex: 0,
                    child: Text(
                        "ABANOUB SALIB",
                      style: TextStyle(
                        color: Colors.blueGrey,
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ) ,
                    ),
                  )
                ],
            ),
          ),
        );
      }
    }


