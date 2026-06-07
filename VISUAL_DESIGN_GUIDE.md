# 🎨 YunTech Modern Design - Visual Guide

## Design System Overview

This document provides a comprehensive visual reference for the modern redesign of the YunTech Campus Activity Registration Platform.

---

## 📐 Color System

### Primary Colors

```
Primary Green        #0A5338  RGB(10, 83, 56)     ████████████
Secondary Green      #2E8B57  RGB(46, 139, 87)    ████████████
Accent Blue          #009CF7  RGB(0, 156, 247)    ████████████
```

### Supporting Colors

```
Light Background     #F8FAFC  RGB(248, 250, 252)  ████████████
Card White           #FFFFFF  RGB(255, 255, 255)  ████████████
Text Primary         #1E293B  RGB(30, 41, 59)     ████████████
Text Secondary       #64748B  RGB(100, 116, 139)  ████████████
Text Tertiary        #94A3B8  RGB(148, 163, 184)  ████████████
Border Light         #E2E8F0  RGB(226, 232, 240)  ████████████
```

### Status Colors

```
Success Green        #10B981  RGB(16, 185, 129)   ████████████
Error Red            #EF4444  RGB(239, 68, 68)    ████████████
Warning Orange       #F59E0B  RGB(245, 158, 11)   ████████████
```

---

## 🔤 Typography System

### Font Stack
```css
font-family: "Microsoft JhengHei", "Segoe UI", "Apple SD Gothic Neo", sans-serif;
```

### Text Sizes & Weights

| Role | Size | Weight | Usage |
|------|------|--------|-------|
| Hero Title | 42px | 700 | Main banner heading |
| Page Title | 32px | 700 | Section headers |
| Card Title | 16px | 700 | Activity card title |
| Body Text | 13-14px | 400-500 | Regular content |
| Small Text | 11-12px | 500 | Labels, badges |

### Line Heights
- Titles: 1.2x
- Body: 1.5x
- Headings: 1.3x

---

## 🎯 Component Styles

### 1. Navigation Bar
```
Background: #FFFFFF
Shadow: dropshadow(3-pass-box, rgba(10,83,56,0.08), 16, 0, 0, 4)
Height: 80px
Border Bottom: 1px #F1F5F9
```

**Logo**: 45x45px, circular clip, green background
**Menu Items**: 15px text, hover background #rgba(10,83,56,0.12)

### 2. Hero Banner
```
Background: linear-gradient(135deg, #0A5338 0%, #2E8B57 100%)
Height: 280px
Padding: 60px 32px 40px 32px
```

**Title**: 42px, 700 weight, white text
**Subtitle**: 16px, 300 weight, #d0fcd0 color

### 3. Search Panel
```
Background: rgba(255, 255, 255, 0.85) (glassmorphism)
Border-radius: 20px
Padding: 28px 32px
Shadow: dropshadow(3-pass-box, rgba(10,83,56,0.12), 20, 0, 0, 8)
Border: 1px rgba(255, 255, 255, 0.6)
Margin: 0 32px 28px 32px
```

**Search Field**:
- Padding: 14px 18px
- Border-radius: 14px
- Border: 1.5px #E2E8F0
- Width: 500px (max 600px)
- Focus: border-color #0a5338, enhanced shadow

**Sort Combo**:
- Width: 180px
- Same styling as search field
- Focus: border-color #0a5338

### 4. Activity Cards (Grid Layout)

**Container**:
- Gap: 24px (horizontal), 24px (vertical)
- Padding: 0 32px 40px 32px
- 3-column responsive grid

**Card**:
```
Background: rgba(255, 255, 255, 0.9)
Border-radius: 20px
Padding: 24px 20px
Shadow: dropshadow(3-pass-box, rgba(0,0,0,0.08), 16, 0, 0, 4)
Border: 1px rgba(255, 255, 255, 0.5)
Hover Shadow: dropshadow(3-pass-box, rgba(10,83,56,0.15), 24, 0, 0, 8)
```

**Card Content**:
- Title: 16px, 700, #1E293B
- Info: 13px, #64748B
- Labels: #94A3B8
- Values: 14px, 600, #334155

### 5. Buttons

**Primary Button**
```
Background: #0a5338
Text: White, 14px, 600 weight
Padding: 12px 28px
Border-radius: 12px
Shadow: dropshadow(3-pass-box, rgba(10,83,56,0.2), 10, 0, 0, 3)
Hover: #08643c, enhanced shadow
```

**Accent Button**
```
Background: #009CF7 (Blue)
Text: White, 14px, 600 weight
Padding: 12px 28px
Border-radius: 12px
Shadow: dropshadow(3-pass-box, rgba(0,156,247,0.2), 10, 0, 0, 3)
Hover: #0080d0
```

**Secondary Button**
```
Background: #f1f5f9
Text: #475569, 13px, 600 weight
Padding: 8px 16px
Border-radius: 10px
Border: 1px #E2E8F0
Hover: #e2e8f0
```

**Danger Button**
```
Background: #ef4444
Text: White, 13px, 600 weight
Padding: 8px 16px
Border-radius: 10px
Shadow: dropshadow(3-pass-box, rgba(239,68,68,0.2), 8, 0, 0, 2)
Hover: #dc2626, enhanced shadow
```

### 6. Tables

**Table Container**:
```
Background: transparent
Control-inner: #ffffff
Alt-rows: #f8fafc
Border-radius: 14px
Border: 1px #E2E8F0
Shadow: dropshadow(3-pass-box, rgba(0,0,0,0.06), 14, 0, 0, 3)
```

**Header**:
```
Background: linear-gradient(135deg, #0A5338 0%, #2E8B57 100%)
Height: 48px
Text: White, 13px, 600 weight, centered
Border: None
```

**Rows**:
```
Height: 54px
Padding: 14px
Border-bottom: 1px #F1F5F9
Text: #475569
Alternate: #fafbfc, #ffffff
Hover: #f0f9ff
Selected: #e0f2fe background, #0a5338 text
```

### 7. Status Badges

```
Padding: 4px 10px
Border-radius: 8px
Font-size: 12px
Font-weight: 700
Text: White
```

**Open/Active**: #10B981 (Green)
**Closed**: #EF4444 (Red)
**Pending**: #F59E0B (Orange)

### 8. Detail Card

```
Background: #ffffff
Border-radius: 18px
Padding: 32px 40px
Shadow: dropshadow(3-pass-box, rgba(0,0,0,0.08), 16, 0, 0, 4)
Border: 1px #F1F5F9
Spacing: 16px between elements
```

**Detail Title**: 28px, 700, #0A5338
**Detail Label**: 13px, 600, #64748B
**Detail Content**: 14px, #334155, background #f8fafc

### 9. Login Card

```
Background: #ffffff
Border-radius: 20px
Padding: 48px 56px
Shadow: dropshadow(3-pass-box, rgba(0,0,0,0.1), 20, 0, 0, 4)
Max-width: 420px
Centered in viewport
```

**Title**: 24px, bold, #0A5338
**Label**: 14px, bold, #334155
**Input Field**:
- Padding: 10px 14px
- Border-radius: 10px
- Border: 1px #E2E8F0
- Focus: border #0a5338

---

## ✨ Visual Effects

### Shadows
- **Soft Shadow**: `dropshadow(3-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 2)`
- **Medium Shadow**: `dropshadow(3-pass-box, rgba(0,0,0,0.08), 16, 0, 0, 4)`
- **Strong Shadow**: `dropshadow(3-pass-box, rgba(10,83,56,0.2), 10, 0, 0, 3)`
- **Hover Shadow**: `dropshadow(3-pass-box, rgba(10,83,56,0.15), 24, 0, 0, 8)`

### Gradients
- **Primary Gradient**: `linear-gradient(135deg, #0A5338 0%, #2E8B57 100%)`
- **Angle**: 135 degrees (diagonal)
- **Start**: Primary Green
- **End**: Secondary Green

### Glassmorphism
- **Technique**: Semi-transparent background + border
- **Background**: rgba(255, 255, 255, 0.85) - 85% opaque white
- **Border**: 1px rgba(255, 255, 255, 0.6) - 60% opaque white
- **Effect**: Frosted glass appearance

### Rounded Corners
- **Button**: 10-12px
- **Card**: 18-20px
- **Input**: 10-14px
- **Table**: 14px

---

## 📏 Spacing System

### Padding
- **Small**: 8px
- **Medium**: 12-16px
- **Large**: 24-28px
- **Extra Large**: 32-40px
- **Component**: 28-32px horizontal

### Margins & Gaps
- **Between items**: 12-16px
- **Between sections**: 20-28px
- **Grid gap**: 24px
- **Header gap**: 16px

### Size References
- **Icon size**: 45x45px (logo)
- **Button height**: Auto (based on padding)
- **Input height**: 44-48px
- **Row height**: 54px (tables)
- **Banner height**: 280px (hero)
- **Navbar height**: 80px

---

## 🎬 Interactions

### Hover States
- Background color shifts
- Shadow intensifies
- Border/text color changes
- Opacity adjustments

### Focus States
- Border color changes to #0a5338
- Shadow appears
- Visual indication of interactive element

### Active States
- Darker background
- Stronger shadow
- Visual feedback

---

## 📱 Responsive Breakpoints

### Primary Breakpoint
- **Desktop**: 1920x1080 (optimized resolution)
- **Adaptive**: Scales based on window size
- **DPI**: Standard and high DPI support

---

## 🎓 Brand Personality

The design embodies:
- **Modern** - Contemporary, clean aesthetics
- **Professional** - Academic and trustworthy
- **Accessible** - Clear hierarchy and contrast
- **Efficient** - Minimal but purposeful design
- **Tech-Forward** - Modern technology university feel
- **Apple-inspired** - Minimalist, elegant approach
- **Material Design** - Depth, shadows, meaningful motion

---

## 📋 Quick Reference

| Element | Color | Size | Radius |
|---------|-------|------|--------|
| Navbar | #FFFFFF | 80px height | N/A |
| Hero Banner | Gradient | 280px height | N/A |
| Primary Button | #0A5338 | 14px text | 12px |
| Accent Button | #009CF7 | 14px text | 12px |
| Card | #FFFFFF | Variable | 20px |
| Table Header | Gradient | 48px height | 14px |
| Badge | Variable | 12px text | 8px |
| Input | #FFFFFF | 44px height | 14px |

---

**Design Version**: 1.0 Modern Redesign
**Last Updated**: 2026-06-08
**Framework**: JavaFX 24.0.1
**Design Approach**: Apple-like Minimalism + Material Design + Glassmorphism
