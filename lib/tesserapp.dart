import 'package:flutter/widgets.dart';
import 'package:flutter/material.dart';
import 'package:tesserapp/home.dart';

class TesserApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => MaterialApp(
    title: "Tesserapp",
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.deepOrange,
        accentColor: Colors.white,
      ),
      home: Home(),
  );
}
