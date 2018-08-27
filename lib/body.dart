import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class Body extends StatelessWidget {
  final ValueChanged<bool> onDarkThemeSelected;
  final ValueChanged<bool> onDebugPaintSizeSelected;

  const Body(
      {Key key,
      @required this.onDarkThemeSelected,
      @required this.onDebugPaintSizeSelected})
      : super(key: key);

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
          DarkThemeCheckbox(onChanged: onDarkThemeSelected),
          DebugPaintSizeOption(onChanged: onDebugPaintSizeSelected),
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
  final ValueChanged<bool> onChanged;

  DarkThemeCheckbox({Key key, this.onChanged}) : super(key: key);

  @override
  _DarkThemeCheckboxState createState() => _DarkThemeCheckboxState(onChanged);
}

class _DarkThemeCheckboxState extends State<DarkThemeCheckbox> {
  final ValueChanged<bool> onChanged;
  var enabled = ValueNotifier<bool>(true);

  _DarkThemeCheckboxState(this.onChanged) {
    enabled.addListener(() {
      onChanged(enabled.value);
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

class DebugPaintSizeOption extends StatefulWidget {
  final ValueChanged<bool> onChanged;

  DebugPaintSizeOption({Key key, this.onChanged}) : super(key: key);

  @override
  _DebugPaintSizeOptionState createState() =>
      _DebugPaintSizeOptionState(onChanged);
}

class _DebugPaintSizeOptionState extends State<DebugPaintSizeOption> {
  final ValueChanged<bool> onChanged;
  var enabled = ValueNotifier<bool>(false);

  _DebugPaintSizeOptionState(this.onChanged) {
    enabled.addListener(() {
      onChanged(enabled.value);
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
              Text("Debug Paint Size"),
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
