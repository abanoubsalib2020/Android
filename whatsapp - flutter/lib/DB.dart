import 'package:sqflite/sqflite.dart';
import 'package:whatsapp/Conversation.dart';
import 'package:whatsapp/Message.dart';
import 'Conversation.dart';

class DB {

  static Database database;

  static void open_database() async {
    var databasesPath = await getDatabasesPath();
    String path = databasesPath + 'whats.db';
    print(path);
    // open the database
    database = await openDatabase(path, version: 1,
        onCreate: (Database db, int version) async {
          await db.execute(
              'CREATE TABLE Conversations (ConversationNumber TEXT  PRIMARY KEY, ConversationName TEXT ,LastMessage TEXT, Time TEXT)');

          // await db.execute(
          //   'CREATE TABLE Message (ConversationNumber TEXT  PRIMARY KEY, content TEXT ,MessageWriter TEXT, Date TEXT,)');

          print("done");
        });
  }

  static create_Conversation(Conversation conversation) async
  {
    String ConversationName = conversation.ConversationName;
    String ConversationNumber = conversation.ConversationNumber;
    String LastMessage = conversation.LastMessage;
    String time = conversation.Time;
    if(ConversationName == "" || ConversationName == null  )
      ConversationName = ConversationNumber;

    await open_database();
    String sql = 'INSERT INTO Conversations(ConversationName,ConversationNumber, LastMessage, Time) VALUES("$ConversationName","$ConversationNumber", "$LastMessage", "$time") ';
    await database.transaction((txn) async
    {
      int n = await txn.rawInsert(sql);
    }
    );
  }

  static create_message_table(String Conversation_number) async
  {
    String Conversation_table = 'T' + Conversation_number;
    await open_database();
    String sql = 'CREATE TABLE IF NOT EXISTS $Conversation_table (id  INTEGER  PRIMARY KEY AUTOINCREMENT, content   TEXT ,MessageWriter TEXT, Time TEXT)';
    database.execute(sql);

  }


  static Future<List<Conversation>> get_all_Conversation() async
  {
    await open_database();
    List<Map> conversations_rows = await database.rawQuery(
        'SELECT * FROM Conversations');
    print(conversations_rows);
    List<Conversation> output = [];
    for (int i = 0; i < conversations_rows.length; i++) {
      Conversation current = new Conversation(
          ConversationNumber: conversations_rows[i]['ConversationNumber'],
          LastMessage: conversations_rows[i]['LastMessage'],
          Time: conversations_rows[i]['Time']);
      current.ConversationName = conversations_rows[i]['ConversationName'];
      output.add(current);
    }

   return output;
  }


  static update_conversation_last_message(Message message, String Conversation_number) async
  {

    await open_database();
    String content = message.content;
    String Date = message.Date;


    String sql = 'UPDATE Conversations SET LastMessage = "$content" , Time = "$Date" WHERE ConversationNumber = "$Conversation_number"';
    print(content);
    int count = await database.rawUpdate(sql);
    print('updated: $count');

  }


  static get_all_messages(String Conversation_Table) async
  {
    await open_database();
    List<Map> messages_rows = await database.rawQuery(
        'SELECT * FROM $Conversation_Table');
    List<Message> output = [];

    for (int i = 0; i < messages_rows.length; i++) {
      output.add(new Message(content: messages_rows[i]['content'],
          MessageWriter: messages_rows[i]['MessageWriter'],
          Date: messages_rows[i]['Time']));
    }
    return output;

  }


  static Insert_message_to_table(Message message, String Conversation_Table) async
  {
    await open_database();
    String content = message.content;
    String Date = message.Date;
    String MessageWriter = message.MessageWriter;

    List<Map> messages_rows = await database.rawQuery(
        'SELECT * FROM $Conversation_Table WHERE Time = "$Date" AND content = "$content"' );
    if (messages_rows.length == 0) {
      String sql = 'INSERT INTO $Conversation_Table(content, MessageWriter, Time) VALUES("$content", "$MessageWriter", "$Date" ) ';
      await database.transaction((txn) async
      {
        int n = await txn.rawInsert(sql);
        print("here");
      }
      );

    }

  }
}