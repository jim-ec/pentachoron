import 'package:flutter/material.dart';

class Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: Row(
            children: <Widget>[Text("Tesserapp")],
          ),
          elevation: 0.0,
        ),
      );
}
