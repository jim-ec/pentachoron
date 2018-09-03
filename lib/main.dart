import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:tesserapp/appbar.dart';
import 'package:tesserapp/backdrop.dart';
import 'package:tesserapp/backpanel.dart';
import 'package:tesserapp/frontpanel.dart';
import 'package:tesserapp/theme.dart';
import 'package:dynamic_theme/dynamic_theme.dart';

void main() => runApp(TesserApp());

class TesserApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => DynamicTheme(
        defaultBrightness: Brightness.dark,
        data: (brightness) => tesserTheme(brightness),
        themedWidgetBuilder: (context, theme) => MaterialApp(
              title: "Tesserapp",
              debugShowCheckedModeBanner: false,
              theme: theme,
              home: Scaffold(
                appBar: appBar(),
                body: Backdrop(
                  backPanel: BackPanel(),
                  frontPanel: FrontPanel(),
                ),
              ),
            ),
      );
}
