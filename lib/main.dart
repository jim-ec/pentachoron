import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:tesserapp/home.dart';
import 'package:tesserapp/theme.dart';

void main() => runApp(TesserApp());

class TesserApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    debugPaintSizeEnabled = false;
    return MaterialApp(
      title: "Tesserapp",
      debugShowCheckedModeBanner: false,
      theme: tesserTheme(),
      home: Home(),
    );
  }
}
