import 'package:flutter/material.dart';

class FrontPanel extends StatelessWidget {
  @override
  Widget build(BuildContext context) => Material(
        borderRadius: BorderRadius.vertical(top: Radius.circular(32.0)),
        child: Center(
          child: Text("Front Panel"),
        ),
      );
}
