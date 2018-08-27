import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class Body extends StatelessWidget {
  final ValueChanged<bool> onDarkThemeSelected;

  const Body({Key key, @required this.onDarkThemeSelected}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final themes = Theme.of(context);
    return Container(
      color: themes.primaryColor,
      width: double.infinity,
      child: Column(
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(
              "Geometry",
              style: themes.textTheme.caption,
            ),
          ),
          Row(
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              Column(
                children: <Widget>[
                  Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Text("16 Vertices"),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Text("32 Edges"),
                  ),
                ],
              ),
              Column(
                children: <Widget>[
                  Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Text("24 Faces"),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Text("8 Cells"),
                  ),
                ],
              ),
            ],
          ),
          Divider(
            height: 5.0,
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(
              "Options",
              style: themes.textTheme.caption,
            ),
          ),
          DarkThemeCheckbox(onDarkThemeSelected: onDarkThemeSelected),
          Divider(
            height: 5.0,
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(
              "Credits",
              style: themes.textTheme.caption,
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Text(
                "My hobby project to play with four dimensional spatials.\n"
                "The app is written with Flutter.\n\n"
                "The source code is freely available at my GitHub repository."),
          ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: FlatButton(
              child: Text("Source code".toUpperCase()),
              onPressed: _launchUrl,
            ),
          )
        ],
      ),
    );
  }
}

class DarkThemeCheckbox extends StatefulWidget {
  final ValueChanged<bool> onDarkThemeSelected;

  DarkThemeCheckbox({Key key, this.onDarkThemeSelected}) : super(key: key);

  @override
  _DarkThemeCheckboxState createState() =>
      _DarkThemeCheckboxState(onDarkThemeSelected);
}

class _DarkThemeCheckboxState extends State<DarkThemeCheckbox> {
  final ValueChanged<bool> onDarkThemeSelected;
  var enabled = ValueNotifier<bool>(true);

  _DarkThemeCheckboxState(this.onDarkThemeSelected) {
    enabled.addListener(() {
      onDarkThemeSelected(enabled.value);
      setState(() {});
    });
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      type: MaterialType.transparency,
      child: InkWell(
        onTap: () {
          setState(() {
            enabled.value = !enabled.value;
          });
        },
        child: Container(
          padding: const EdgeInsets.all(16.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              Text("Dark Theme"),
              Checkbox(
                value: enabled.value,
                onChanged: (checked) {
                  setState(() {
                    enabled.value = checked;
                  });
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}

_launchUrl() async {
  const url = "https://github.com/Jim-Eckerlein/tesserapp";
  if (await canLaunch(url)) {
    await launch(url);
  } else {
    print("Cannot launch url");
  }
}
