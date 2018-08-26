import 'package:flutter/material.dart';
import 'package:tesserapp/common.dart';

class Body extends StatelessWidget {
  @override
  Widget build(BuildContext context) => center(
        Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: <Widget>[
            Text("Hello world"),
            Text("Hello world", style: Theme.of(context).textTheme.subhead),
            FlatButton(
              child: Text("Flat Button".toUpperCase()),
              onPressed: () {},
            ),
            RaisedButton(
              child: Text("Raised button".toUpperCase()),
              onPressed: () {},
            ),
            RaisedButton(
              child: Text("Primary button".toUpperCase()),
              onPressed: () {},
              color: Theme.of(context).primaryColor,
              textTheme: ButtonTextTheme.primary,
            ),
          ],
        ),
      );
}
