import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:tesserapp/appbar.dart';
import 'package:tesserapp/body.dart';
import 'package:tesserapp/theme.dart';

void main() => runApp(TesserApp());

class TesserApp extends StatefulWidget {
  @override
  _TesserAppState createState() => _TesserAppState();
}

class _TesserAppState extends State<TesserApp> {
  
  bool darkThemeEnabled = true;
  
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "Tesserapp",
      debugShowCheckedModeBanner: false,
      theme: tesserTheme(darkThemeEnabled),
      home: Scaffold(
        appBar: appBar(),
        body: BackdropContent(
          onOptionsChanged: (options) {
            setState(() {
              darkThemeEnabled = options.darkThemeEnabled;
            });
          },
        ),
      ),
    );
  }
}
