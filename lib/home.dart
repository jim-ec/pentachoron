import 'package:flutter/material.dart';
import 'package:tesserapp/appbar.dart';
import 'package:tesserapp/backdrop.dart';
import 'package:tesserapp/backpanel.dart';
import 'package:tesserapp/frontpanel.dart';

class Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) => Scaffold(
        body: Container(
          color: Theme.of(context).primaryColor,
          child: SafeArea(
            child: Backdrop(
              backBar: TopBar(),
              backPanel: BackPanel(),
              frontPanel: FrontPanel(),
            ),
          ),
        ),
      );
}
