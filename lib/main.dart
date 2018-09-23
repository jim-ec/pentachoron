import 'package:dynamic_theme/dynamic_theme.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:Pentachoron/app_options.dart';
import 'package:Pentachoron/home.dart';
import 'package:Pentachoron/theme.dart';

void main() => runApp(PentachoronApp());

class PentachoronApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => DynamicTheme(
        defaultBrightness: Brightness.dark,
        data: (brightness) => tesserTheme(brightness),
        themedWidgetBuilder: (context, theme) => MaterialApp(
              title: "Pentachoron",
              theme: theme,
              home: AppOptionsProvider(child: Home()),
            ),
      );
}
