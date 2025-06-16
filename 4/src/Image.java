import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class Image
{

    interface Command
    {
        void execute(File imageFile) throws IOException;
    }

    /**
     * Команда растяжения (Stretch).
     */
    static class StretchCommand implements Command
    {
        private final double factor;

        public StretchCommand(double factor)
        {
            this.factor = factor;
        }

        @Override
        public void execute(File imageFile) throws IOException
        {
            System.out.println("   [Stretch] Файл: " + imageFile.getName() + " Коэффициент: " + factor);
            BufferedImage original = ImageIO.read(imageFile);
            int newWidth = (int) (original.getWidth() * factor);
            int newHeight = (int) (original.getHeight() * factor);

            BufferedImage resized = new BufferedImage(newWidth, newHeight, original.getType());
            Graphics2D g = resized.createGraphics();
            g.drawImage(original, 0, 0, newWidth, newHeight, null);
            g.dispose();

            ImageIO.write(resized, "png", imageFile);
        }

    }

    /**
     * Команда создания негатива изображения (Negative).
     */
    static class NegativeCommand implements Command {
        @Override
        public void execute(File imageFile) throws IOException {
            System.out.println("   [Negative] Файл: " + imageFile.getName());
            BufferedImage original = ImageIO.read(imageFile);
            int width = original.getWidth();
            int height = original.getHeight();

            BufferedImage negative = new BufferedImage(width, height, original.getType());

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(original.getRGB(x, y));
                    // Инвертируем каждый цветовой канал
                    Color negativeColor = new Color(
                            255 - color.getRed(),
                            255 - color.getGreen(),
                            255 - color.getBlue()
                    );
                    negative.setRGB(x, y, negativeColor.getRGB());
                }
            }

            ImageIO.write(negative, "png", imageFile);
        }
    }

    /**
     * Команда удаления (Remove).
     */
    static class RemoveCommand implements Command
    {
        private Path backupPath;

        @Override
        public void execute(File imageFile) throws IOException
        {
            backupPath = Files.createTempFile("backup_", "_" + imageFile.getName());
            Files.copy(imageFile.toPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("   [Remove] Файл: " + imageFile.getName());
            Files.deleteIfExists(imageFile.toPath());
        }


    }

    /**
     * Команда копирования (Copy).
     */
    static class CopyCommand implements Command
    {
        private final String targetDir;

        public CopyCommand(String targetDir)
        {
            this.targetDir = targetDir;
        }

        @Override
        public void execute(File imageFile) throws IOException
        {
            Path targetPath = Paths.get(targetDir, imageFile.getName());
            Files.copy(imageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("   [Copy] Файл: " + imageFile.getName() + " -> " + targetDir);
        }


    }

    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("Ошибка: не указан путь к каталогу.");
            return;
        }

        String sourceDir = args[0];
        File root = new File(sourceDir);
        if (!root.exists() || !root.isDirectory())
        {
            System.err.println("Ошибка: указанный путь не является каталогом.");
            return;
        }

        List<Command> commands = new ArrayList<>();
        boolean subMode = false;
        int i = 1;

        while (i < args.length)
        {
            switch (args[i])
            {
                case "/sub":
                    subMode = true;
                    i++;
                    break;
                case "/s":
                    i++;
                    if (i >= args.length)
                    {
                        System.err.println("Ошибка: для /s требуется коэффициент.");
                        return;
                    }
                    try {
                        double factor = Double.parseDouble(args[i]);
                        commands.add(new StretchCommand(factor));
                    } catch (NumberFormatException e)
                    {
                        System.err.println("Ошибка: коэффициент для /s должен быть числом.");
                        return;
                    }
                    i++;
                    break;
                case "/n":
                    commands.add(new NegativeCommand());
                    i++;
                    break;
                case "/r":
                    commands.add(new RemoveCommand());
                    i++;
                    break;
                case "/c":
                    i++;
                    if (i >= args.length)
                    {
                        System.err.println("Ошибка: для /c требуется путь.");
                        return;
                    }
                    commands.add(new CopyCommand(args[i]));
                    i++;
                    break;
                default:
                    System.err.println("Неизвестный аргумент: " + args[i]);
                    return;
            }
        }

        List<File> imageFiles = new ArrayList<>();
        if (subMode)
        {
            collectImagesRecursively(root, imageFiles);
        } else {
            File[] files = root.listFiles();
            if (files != null)
            {
                for (File f : files)
                {
                    if (isImageFile(f))
                    {
                        imageFiles.add(f);
                    }
                }
            }
        }

        System.out.println("Обработка " + imageFiles.size() + " изображений...");
        for (File image : imageFiles)
        {
            for (Command cmd : commands)
            {
                try {
                    cmd.execute(image);
                } catch (IOException e)
                {
                    System.err.println("Ошибка при обработке " + image.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private static void collectImagesRecursively(File dir, List<File> imageFiles)
    {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files)
        {
            if (f.isDirectory())
            {
                collectImagesRecursively(f, imageFiles);
            } else if (isImageFile(f))
            {
                imageFiles.add(f);
            }
        }
    }

    private static boolean isImageFile(File file)
    {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".png");
    }
}