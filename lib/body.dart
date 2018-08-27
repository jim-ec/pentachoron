import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class Body extends StatelessWidget {
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
          _DarkThemeCheckbox(),
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

class _DarkThemeCheckbox extends StatefulWidget {
  @override
  _DarkThemeCheckboxState createState() => _DarkThemeCheckboxState();
}

class _DarkThemeCheckboxState extends State<_DarkThemeCheckbox> {
  
  bool enabled = true;

  @override
  Widget build(BuildContext context) {
    final themes = Theme.of(context);
    return Material(
      color: themes.primaryColor,
      child: InkWell(
        onTap: () {
          setState(() {
            enabled = !enabled;
          });
        },
        child: Container(
          padding: const EdgeInsets.all(16.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              Text("Dark Theme"),
              Checkbox(
                value: enabled,
                onChanged: (checked) {
                  setState(() {
                    enabled = checked;
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
