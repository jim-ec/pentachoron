import 'package:flutter/material.dart';

ThemeData tesserTheme(final Brightness brightness) => ThemeData(
      primaryColor: Colors.deepOrangeAccent[400],
      toggleableActiveColor: Colors.deepOrangeAccent[700],
      accentColor:  Colors.deepOrangeAccent[700],
      fontFamily: "GeoNMS",
      brightness: brightness,
      textTheme: TextTheme(
        body1: TextStyle(
          letterSpacing: 0.8,
        ),
        button: TextStyle(
          fontWeight: FontWeight.w900,
          letterSpacing: 1.2,
        ),
      ),
      primaryTextTheme: TextTheme(
        title: TextStyle(
          fontSize: 24.0,
          letterSpacing: 1.2,
        ),
        body1: TextStyle(
          letterSpacing: 0.8,
        ),
        button: TextStyle(
          fontWeight: FontWeight.w900,
          letterSpacing: 1.2,
        ),
      ),
    );
