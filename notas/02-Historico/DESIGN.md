---
name: FreshCart Logic
colors:
  surface: '#fbf9f9'
  surface-dim: '#dbdad9'
  surface-bright: '#fbf9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f3'
  surface-container: '#efeded'
  surface-container-high: '#e9e8e7'
  surface-container-highest: '#e3e2e2'
  on-surface: '#1b1c1c'
  on-surface-variant: '#40493d'
  inverse-surface: '#303031'
  inverse-on-surface: '#f2f0f0'
  outline: '#707a6c'
  outline-variant: '#bfcaba'
  surface-tint: '#1b6d24'
  primary: '#0d631b'
  on-primary: '#ffffff'
  primary-container: '#2e7d32'
  on-primary-container: '#cbffc2'
  inverse-primary: '#88d982'
  secondary: '#596055'
  on-secondary: '#ffffff'
  secondary-container: '#dee5d6'
  on-secondary-container: '#5f665b'
  tertiary: '#1f6223'
  on-tertiary: '#ffffff'
  tertiary-container: '#3a7b39'
  on-tertiary-container: '#c8ffbf'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#a3f69c'
  primary-fixed-dim: '#88d982'
  on-primary-fixed: '#002204'
  on-primary-fixed-variant: '#005312'
  secondary-fixed: '#dee5d6'
  secondary-fixed-dim: '#c2c9bb'
  on-secondary-fixed: '#171d14'
  on-secondary-fixed-variant: '#42493e'
  tertiary-fixed: '#acf4a4'
  tertiary-fixed-dim: '#91d78a'
  on-tertiary-fixed: '#002203'
  on-tertiary-fixed-variant: '#0c5216'
  background: '#fbf9f9'
  on-background: '#1b1c1c'
  surface-variant: '#e3e2e2'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 26px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  body-md-strikethrough:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  touch-target: 48px
  margin-edge: 16px
  gutter: 12px
  list-item-height: 64px
  bottom-nav-height: 80px
---

## Brand & Style

The design system is anchored in **Functional Minimalism** with a focus on high-speed utility and physical ergonomics. Designed for the grocery aisle, the UI prioritizes legibility under varying light conditions and the physical constraints of one-handed operation. 

The aesthetic is "Soft Professional"—combining the systematic rigor of a productivity tool with the approachable warmth of a lifestyle app. It utilizes high-contrast interactive zones against expansive, calm backgrounds to reduce cognitive load while shopping. Motion is integral, using fluid transitions to provide tactile feedback for completed tasks.

## Colors

This design system utilizes a "Grocery Green" primary palette to evoke freshness and health. 

- **Primary Canvas:** In light mode, use pure white (#FFFFFF) for surfaces and a very light cool gray (#F5F5F5) for the background to create subtle separation. In dark mode, shift to a deep Slate Navy (#121212) to maintain contrast without screen glare.
- **Interaction States:** Use the Primary Green for active states and FABs. Completed items should transition to a 40% opacity of the neutral text color.
- **Categorization:** Use high-chroma, low-saturation "Pastel Tints" for category chips. These should be paired with dark-toned text from the same color family to ensure AA accessibility.

## Typography

The system relies on **Inter** for its exceptional legibility and modern, neutral character. 

- **Hierarchy:** Use `Display-LG` for list titles and `Body-LG` for primary list items to ensure they are readable at arm's length.
- **States:** Checked items must immediately switch to `body-md-strikethrough` with reduced opacity to visually "recede" from the user's focus.
- **Mobile Scaling:** Headlines larger than 24px should scale down by 15% on small device breakpoints (width < 360dp) to prevent awkward text wrapping in list rows.

## Layout & Spacing

This design system follows a **Bottom-Weighted Fluid Grid**. 

- **Ergonomics:** Key actions (Add Item, Checkout, Category Filter) are anchored to the bottom 30% of the screen. The Floating Action Button (FAB) is oversized (56dp+) and positioned in the bottom-right or bottom-center.
- **Grid:** Use a 4-column fluid grid for mobile. Margins are fixed at 16dp.
- **Vertical Rhythm:** Every element follows an 8dp baseline grid. List items are strictly 64dp or 72dp in height to ensure generous touch targets, preventing accidental taps in a busy store environment.
- **Safe Areas:** Ensure a minimum of 80dp bottom padding on all scrollable lists to prevent the FAB from obscuring the final item.

## Elevation & Depth

Visual hierarchy is established through **Tonal Layers** and **Soft Shadows**.

- **Surfaces:** The main background is level 0. Cards and list containers sit at level 1, using a subtle 1dp border or a very soft, diffused shadow (Blur: 8dp, Y: 2dp, Opacity: 4%).
- **Interactive Elements:** Buttons and active FABs use a "Medium Elevation" (Level 2) to appear physically pressable. 
- **Transitions:** Use shadow expansion (Level 1 to Level 3) during drag-and-drop reordering of list items to indicate the item has been "picked up" from the surface.

## Shapes

The shape language is defined by **Large Radii (16dp+)** to create a friendly, modern feel.

- **Cards & Sheets:** Use 16dp (`rounded-lg`) for all main container corners. Bottom sheets should only have rounding on the top two corners (24dp).
- **Chips:** Category chips use a fully rounded "Pill" shape (height/2) to distinguish them from actionable buttons.
- **Inputs:** Text fields use 12dp rounding to balance the "softness" of the UI with the "precision" of data entry.

## Components

- **List Items:** Must be at least 64dp high. Include a leading checkbox (24dp) and a trailing "Drag Handle" or "Delete" swipe action. Use a horizontal divider of 1dp with 10% opacity, inset by 56dp to align with the text.
- **FAB (Floating Action Button):** The primary interaction point. Use the Primary Green background with a white icon. Include an "Extended FAB" variant that collapses into a circle upon scrolling down.
- **Category Chips:** Small, non-bordered containers with pastel backgrounds. Active state is indicated by a 2dp border in a darker shade of the same hue.
- **Input Fields:** Outlined style with a 1.5dp stroke. When focused, the stroke thickens and changes to Primary Green. Place labels above the field, never as disappearing placeholders.
- **Bottom Bar:** A custom container for navigation and quick-filters. Use a blur effect (Backdrop Filter) if the list content scrolls behind it.
- **Empty States:** Use simplified, monochromatic illustrations with a clear "Add First Item" call-to-action button in the center of the screen.