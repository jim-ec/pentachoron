import 'package:flutter/material.dart';

typedef void Toggle();

typedef Widget AppOptionsWidgetBuilder(
  final BuildContext context,
  final bool inverted,
  final Toggle toggle,
);

class AppOptionsProvider extends StatefulWidget {
  final AppOptionsWidgetBuilder builder;

  const AppOptionsProvider({Key key, this.builder}) : super(key: key);

  @override
  _AppOptionsState createState() => _AppOptionsState();
}

class _AppOptionsState extends State<AppOptionsProvider> {
  bool _invertedHorizontalCamera = false;

  void toggleInvertedHorizontalCamera() {
    setState(() {
      _invertedHorizontalCamera = !_invertedHorizontalCamera;
    });
  }

  @override
  Widget build(BuildContext context) => AppOptions(
      invertedHorizontalCamera: _invertedHorizontalCamera,
      toggle: toggleInvertedHorizontalCamera,
      child: widget.builder(
        context,
        _invertedHorizontalCamera,
        toggleInvertedHorizontalCamera,
      ),
    );
}

class AppOptions extends InheritedWidget {
  final bool invertedHorizontalCamera;
  final Function toggle;

  const AppOptions({
    Key key,
    @required this.invertedHorizontalCamera,
    @required this.toggle,
    @required Widget child,
  })  : assert(child != null),
        super(key: key, child: child);

  static AppOptions of(BuildContext context) {
    return context.inheritFromWidgetOfExactType(AppOptions);
  }

  @override
  bool updateShouldNotify(final AppOptions old) =>
      invertedHorizontalCamera != old.invertedHorizontalCamera;
}
