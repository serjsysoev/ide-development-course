# Another IDE

"Simple IDE for editing text"


![alt text](files/demo.gif)

Source 1. Demo video

# Workspaces Viewer

![alt text](files/workspacesViewer.png)

Source 2. Workspaces Viewer

## Feature Overview
- Add / Remove Workspace
- Open Existing Workspaces


# Editor

![alt text](files/editor.png)

Source 3. CodeView (Workspace)


## Feature Overview

### Files Tree
- **Structure Display**: Shows the structure of files.
- **Interactive Navigation**: Allows opening files and navigating to the editor with a click.
- **Dynamic Updates**: Refreshes the tree when files are added or removed from the user's file system.

### Editor
- **Text Editing**: Facilitates editing of text.
- **Syntax Highlighting**: Highlights text in files with a `.ascript` extension.
- **Buffered Text Display**: Supports displaying buffered text, limited to lines.

### Opened Files List
- **File Selection**: Clicking on an item opens the corresponding file.

### Caret
- **Selection Functions**:
    - Replace text.
    - Delete text.
- **Movement Controls**:
    - Using keyboard keys.
    - Using the mouse.

### Shortcuts
- Functions for copying, pasting, extracting, and saving text.

### Notifications
- Displays messages about the success or failure of file saving and opening operations.

### Additional Features
- **Cursor Offset Tracker**: Monitors the cursor's position.
- **Gutters**: Provides space for line numbers.




# Technical Details 
**Main programming language:**  
Kotlin

**UI framework:**  
Kotlin for Jetpack Compose

**Structure for buffered displaying text:**  
Rope

**Running application:**
```
./gradlew run
```


**Project structure:**

```
src
├── main
│   ├── kotlin
│   │   ├── Main.kt # Managing the displayed windows
│   │   ├── ascript # Supporting tokenizing / parsing / ast for ascript language
│   │   │   ├── AScript.kt # Language config
│   │   │   ├── ast
│   │   │   │   ├── AstNode.kt
│   │   │   │   └── AstVisitor.kt
│   │   │   ├── grammar
│   │   │   │   └── Grammar.kt # Grammar for language 
│   │   │   ├── highlighting
│   │   │   │   ├── AScriptHighlightingBuilder.kt
│   │   │   │   └── HighlighterVisitor.kt
│   │   │   └── lexer
│   │   │       ├── AScriptLexer.kt
│   │   │       └── Tokenizer
│   │   │           ├── TokenParsers.kt
│   │   │           └── Tokens.kt
│   │   ├── frontend
│   │   │   ├── HighlightingBuilder.kt
│   │   │   └── Workspace.kt
│   │   ├── language  # Own framework for supporting languages
│   │   │   ├── Language.kt
│   │   │   ├── ast
│   │   │   │   └── Ast.kt
│   │   │   ├── grammar
│   │   │   │   ├── Errors.kt
│   │   │   │   ├── Grammar.kt
│   │   │   │   └── Rule.kt
│   │   │   ├── lexer
│   │   │   │   ├── Lexer.kt
│   │   │   │   └── tokenizer
│   │   │   │       ├── TokenParser.kt
│   │   │   │       └── Tokenizer.kt
│   │   ├── ui
│   │   │   ├── CodeViewer.kt
│   │   │   ├── CodeViewerView.kt
│   │   │   ├── WorkspacesViewerView.kt
│   │   │   ├── common   # Styles for elements
│   │   │   │   ├── Fonts.kt
│   │   │   │   ├── Settings.kt
│   │   │   │   └── Theme.kt
│   │   │   ├── editor
│   │   │   │   ├── Editor.kt
│   │   │   │   ├── EditorEmptyView.kt
│   │   │   │   ├── EditorTabsView.kt
│   │   │   │   ├── EditorView.kt
│   │   │   │   ├── Editors.kt
│   │   │   │   ├── Input.kt  # Pressed Keys handling
│   │   │   │   └── highlighting
│   │   │   │       └── Highlighter.kt
│   │   │   └── filetree
│   │   │       ├── FileTree.kt
│   │   │       └── FileTreeView.kt
│   │   └── util
│   │       ├── File.kt
│   │       ├── VerticalSplittable.kt
│   │       └── rope  # Rope on text implementation 
│   │           ├── ConcatNode.kt
│   │           ├── LeafNode.kt
│   │           ├── LineMetrics.kt
│   │           ├── MetricsCalculator.kt
│   │           ├── Rope.kt
│   │           ├── RopeNode.kt
│   │           └── RopeUtils.kt
│   └── resources
│       ├── font
│       │   ├── jetbrainsmono_bold.ttf
│       │   ├── jetbrainsmono_bold_italic.ttf
│       │   ├── jetbrainsmono_extrabold.ttf
│       │   ├── jetbrainsmono_extrabold_italic.ttf
│       │   ├── jetbrainsmono_italic.ttf
│       │   ├── jetbrainsmono_medium.ttf
│       │   ├── jetbrainsmono_medium_italic.ttf
│       │   └── jetbrainsmono_regular.ttf
│       └── icons
└── test
    └── kotlin
        └── ascript
            └── LexerTest.kt
```
