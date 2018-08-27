import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

/// A scroll behavior disabling the over-scroll glow effect,
/// as that is not desirable in the backdrop's back panel.
class BackdropScrollBehavior extends ScrollBehavior {
  @override
  Widget buildViewportChrome(BuildContext context, Widget child, AxisDirection axisDirection) {
    return child;
  }
}

/// Houses the backdrop's back panel content.
class BackdropContent extends StatelessWidget {
  final ValueChanged<Options> onOptionsChanged;

  const BackdropContent({
    Key key,
    @required this.onOptionsChanged,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final themes = Theme.of(context);
    return Container(
      color: themes.primaryColor,
      width: double.infinity,
      child: ScrollConfiguration(
        behavior: BackdropScrollBehavior(),
        child: Scrollbar(
          child: ListView(
            physics: ClampingScrollPhysics(),
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
              OptionsSection(
                onOptionsChanged: onOptionsChanged,
              ),
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
        ),
      ),
    );
  }
}

class Options {
  bool darkThemeEnabled = true;
  bool debugPaintSizeEnabled = false;
}

class OptionsSection extends StatefulWidget {
  final ValueChanged<Options> onOptionsChanged;

  const OptionsSection({Key key, this.onOptionsChanged}) : super(key: key);

  @override
  _OptionsSectionState createState() => _OptionsSectionState(onOptionsChanged);
}

class _OptionsSectionState extends State<OptionsSection> {
  final ValueChanged<Options> onOptionsChanged;
  final options = Options();

  _OptionsSectionState(this.onOptionsChanged);

  _updateState() {
    setState(() {
      onOptionsChanged(options);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        Material(
          type: MaterialType.transparency,
          child: InkWell(
            onTap: () {
              options.darkThemeEnabled = !options.darkThemeEnabled;
              _updateState();
            },
            child: Container(
              padding:
                  const EdgeInsets.symmetric(vertical: 16.0, horizontal: 64.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  Text("Dark Theme"),
                  Checkbox(
                    value: options.darkThemeEnabled,
                    onChanged: (checked) {
                      options.darkThemeEnabled = checked;
                      _updateState();
                    },
                  ),
                ],
              ),
            ),
          ),
        ),
        Material(
          type: MaterialType.transparency,
          child: InkWell(
            onTap: () {
              options.debugPaintSizeEnabled = !options.debugPaintSizeEnabled;
              _updateState();
            },
            child: Container(
              padding:
                  const EdgeInsets.symmetric(vertical: 16.0, horizontal: 64.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  Text("Debug Paint Size"),
                  Checkbox(
                    value: options.debugPaintSizeEnabled,
                    onChanged: (checked) {
                      options.debugPaintSizeEnabled = checked;
                      _updateState();
                    },
                  ),
                ],
              ),
            ),
          ),
        )
      ],
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
