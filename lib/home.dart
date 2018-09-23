import 'package:flutter/material.dart';
import 'package:Pentachoron/back_layer.dart';
import 'package:Pentachoron/backdrop/backdrop.dart';
import 'package:Pentachoron/front_layer.dart';

class Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: Theme.of(context).primaryColor,
        child: SafeArea(
          child: Backdrop(
            backLayer: BackLayer(),
            frontLayer: FrontLayer(),
          ),
        ),
      ),
    );
  }
}
