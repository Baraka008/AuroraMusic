# Aurora Music Deployment Guide 🚀

To make your 3D website "work" online and allow users to download your app, you must complete these three steps on GitHub.

## 1. Push your code to GitHub
If you haven't pushed the latest changes yet, run these commands in your Android Studio terminal:
```bash
git add .
git commit -m "Switch to main branch and update links"
git push -u origin main
```

## 2. Enable the Website (GitHub Pages)
1. Go to your repository at `https://github.com/Baraka008/AuroraMusic`.
2. Click on **Settings** (top navigation bar).
3. Click on **Pages** in the left-hand sidebar.
4. Under **Build and deployment** > **Branch**:
   - Select `main`.
   - Select `/docs` folder.
5. Click **Save**.
*Your website will be live at `https://Baraka008.github.io/AuroraMusic` within 2-3 minutes.*

## 3. How to provide the Download (APK)
The website's download button currently links to your GitHub **Releases** page. You need to upload your app there:
1. In Android Studio, go to **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
2. Once built, find the `app-debug.apk` (or create a signed Release APK).
3. Go to your GitHub repository page.
4. Click on **Releases** (on the right side of the screen).
5. Click **Create a new release**.
6. Tag it as `v1.0.0`, give it a title (e.g., "Initial Public Release").
7. **Crucial**: Drag and drop your `.apk` file into the "Attach binaries" box.
8. Click **Publish release**.

**Now, when someone clicks "Download APK" on your website, they will see your file and can install it on their phone!**
