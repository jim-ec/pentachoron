import 'package:flutter/widgets.dart';
import 'package:flutter/material.dart';
import 'package:tesserapp/home.dart';
import 'package:tesserapp/theme.dart';

class TesserApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => MaterialApp(
        title: "Tesserapp",
        debugShowCheckedModeBanner: false,
        theme: tesserTheme(),
        home: Home(),
      );
}
