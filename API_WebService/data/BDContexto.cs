using Microsoft.EntityFrameworkCore;

public class BDContexto : DbContext
{
 
    public DbSet<Tienda> Tiendas { get; set; } // 🆕 NUEVA LÍNEA

    public BDContexto(DbContextOptions options) : base(options)
    {
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
       
        modelBuilder.Entity<Tienda>().ToTable("Tienda"); // 🆕 NUEVA LÍNEA
    }
}