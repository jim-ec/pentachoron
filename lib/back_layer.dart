import 'package:flutter/material.dart';
import 'package:tesserapp/app_options.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:dynamic_theme/dynamic_theme.dart';

class BackLayer extends StatelessWidget {

  @override
  Widget build(final BuildContext context) {
//    print("build Inverted=$inverted");
    return ScrollConfiguration(
        behavior: NoGlowScrollBehaviour(),
        child: ListView(
          physics: ClampingScrollPhysics(),
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  "Geometry",
                  style: Theme.of(context).textTheme.caption,
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
              child: Center(
                child: Text(
                  "Options",
                  style: Theme.of(context).textTheme.caption,
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
              value: AppOptions.of(context).invertedHorizontalCamera.actualOption.value,
              onToggled: AppOptions.of(context).invertedHorizontalCamera.actualOption.toggle,
            ),
            Divider(
              height: 5.0,
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Center(
                child: Text(
                  "Credits",
                  style: Theme.of(context).textTheme.caption,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Center(
                child: Text(
                    "My hobby project to play with four dimensional spatials.\n"
                    "The app is written with Flutter.\n\n"
                    "The source code is freely available at my GitHub repository."),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Center(
                child: FlatButton(
                  child: Text("Source code".toUpperCase()),
                  onPressed: () async {
                    const url = "https://github.com/Jim-Eckerlein/tesserapp";
                    if (await canLaunch(url)) {
                      await launch(url);
                    }
                  },
                ),
              ),
            )
          ],
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
                Text(label),
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
