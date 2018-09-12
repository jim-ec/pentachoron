import 'package:flutter/material.dart';
import 'package:tesserapp/Button.dart';
import 'package:tesserapp/app_options.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:dynamic_theme/dynamic_theme.dart';

class BackLayer extends StatelessWidget {
  @override
  Widget build(final BuildContext context) {
    final appOptions = AppOptions.of(context);
    return ScrollConfiguration(
      behavior: NoGlowScrollBehaviour(),
      child: Scrollbar(
        child: ListView(
          physics: ClampingScrollPhysics(),
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  "Geometry",
                  style: Theme.of(context).primaryTextTheme.caption,
                ),
              ),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                Column(
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        "16 Vertices",
                        style: Theme.of(context).primaryTextTheme.body1,
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        "32 Edges",
                        style: Theme.of(context).primaryTextTheme.body1,
                      ),
                    ),
                  ],
                ),
                Column(
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        "24 Faces",
                        style: Theme.of(context).primaryTextTheme.body1,
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        "8 Cells",
                        style: Theme.of(context).primaryTextTheme.body1,
                      ),
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
              child: Center(
                child: Text(
                  "Options",
                  style: Theme.of(context).primaryTextTheme.caption,
                ),
              ),
            ),
            Option(
              label: "Dark Theme",
              value: DynamicTheme.of(context).brightness == Brightness.dark,
              onToggled: () {
                DynamicTheme.of(context).setBrightness(
                    DynamicTheme.of(context).brightness == Brightness.light
                        ? Brightness.dark
                        : Brightness.light);
              },
            ),
            Option(
              label: "Inverted horizontal camera",
              value: appOptions.invertedHorizontalCamera.option.value,
              onToggled: appOptions.invertedHorizontalCamera.option.toggle,
            ),
            Option(
              label: "Inverted vertical camera",
              value: appOptions.invertedVerticalCamera.option.value,
              onToggled: appOptions.invertedVerticalCamera.option.toggle,
            ),
            Divider(
              height: 5.0,
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  "Credits",
                  style: Theme.of(context).primaryTextTheme.caption,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Center(
                child: Text(
                  "My hobby project to play with four dimensional spatials.\n"
                      "The app is written with Flutter.\n\n"
                      "The source code is freely available at my GitHub repository.",
                  style: Theme.of(context).primaryTextTheme.body1,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Center(
                child: Button(
                  child: Text(
                    "Get Source code".toUpperCase(),
                    style: Theme.of(context).primaryTextTheme.button,
                  ),
                  onPressed: () async {
                    const url = "https://github.com/Jim-Eckerlein/tesserapp";
                    if (await canLaunch(url)) {
                      await launch(url);
                    }
                  },
                )
              ),
            )
          ],
        ),
      ),
    );
  }
}

/// A single, toggleable option.
/// No state is carried, the current value has to be defined when creating
/// this widget, and [onToggled] is called upon taps indicating a toggle.
class Option extends StatelessWidget {
  final String label;
  final Function() onToggled;
  final bool value;

  const Option({
    Key key,
    @required this.label,
    @required this.onToggled,
    @required this.value,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => Material(
        type: MaterialType.transparency,
        child: InkWell(
          onTap: () => onToggled(),
          child: Container(
            padding:
                const EdgeInsets.symmetric(vertical: 16.0, horizontal: 64.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: <Widget>[
                Text(
                  label,
                  style: Theme.of(context).primaryTextTheme.body1,
                ),
                Checkbox(
                  value: value,
                  onChanged: (value) => onToggled(),
                ),
              ],
            ),
          ),
        ),
      );
}

/// A scroll behavior disabling the over-scroll glow effect,
/// as that is not desirable in the backdrop's back panel.
class NoGlowScrollBehaviour extends ScrollBehavior {
  @override
  Widget buildViewportChrome(
    final BuildContext context,
    final Widget child,
    final AxisDirection axisDirection,
  ) =>
      child;
}
