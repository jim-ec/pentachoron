import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

typedef Widget AppOptionsWidgetBuilder(
  final BuildContext context,
);

class AppOptionsProvider extends StatefulWidget {
  final AppOptionsWidgetBuilder builder;

  const AppOptionsProvider({Key key, this.builder}) : super(key: key);

  @override
  _AppOptionsState createState() => _AppOptionsState();
}

typedef void OnOptionChanged(VoidCallback fn);

class _SingleStatefulAppOption {
  final String key;
  final OnOptionChanged onChanged;

  bool _value = false;

  set value(value) {
    onChanged(() {
      _value = value;
    });
  }

  get value => _value;

  _SingleStatefulAppOption(
    final String name,
    this.onChanged,
  ) : key = "jim.io.tesserapp.options.$name" {
    SharedPreferences.getInstance().then((final prefs) {
      value = prefs.getBool(key) ?? false;
    });
  }

  void toggle() {
    value = !value;
  }
}

@immutable
class _SingleStatelessAppOption {
  final _SingleStatefulAppOption actualOption;
  final bool creationValue;

  _SingleStatelessAppOption(this.actualOption) : creationValue = actualOption.value;
}

class _AppOptionsState extends State<AppOptionsProvider> {
  _SingleStatefulAppOption _invertedHorizontalCamera;

  _AppOptionsState() {
    _invertedHorizontalCamera = _SingleStatefulAppOption("invert_h_cam", setState);
  }

  @override
  Widget build(BuildContext context) => AppOptions(
        invertedHorizontalCamera:
            _SingleStatelessAppOption(_invertedHorizontalCamera),
        child: widget.builder(
          context,
        ),
      );
}

class AppOptions extends InheritedWidget {
  final _SingleStatelessAppOption invertedHorizontalCamera;

  const AppOptions({
    Key key,
    @required this.invertedHorizontalCamera,
    @required Widget child,
  })  : assert(child != null),
        super(key: key, child: child);

  static AppOptions of(BuildContext context) {
    return context.inheritFromWidgetOfExactType(AppOptions);
  }

  @override
  bool updateShouldNotify(final AppOptions old) =>
      invertedHorizontalCamera.creationValue !=
      old.invertedHorizontalCamera.creationValue;
}
