using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

public class Tienda
{
    [Key]
    public int IdTienda { get; set; }

    [Required(ErrorMessage = "El nombre es obligatorio")]
    [StringLength(100)]
    public string Nombre { get; set; }

    [StringLength(150)]
    public string Ubicacion { get; set; }

    public double DistanciaKm { get; set; }

    [StringLength(250)]
    public string Foto { get; set; }

    [StringLength(250)]
    public string Ruta { get; set; }

    [NotMapped]
    public IFormFile Archivo { get; set; }
}