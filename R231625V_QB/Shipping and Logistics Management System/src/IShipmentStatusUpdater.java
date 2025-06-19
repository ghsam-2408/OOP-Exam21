public interface IShipmentStatusUpdater {
    void modifyShipmentStatus(ShipmentDetails shipmentDetails, String status);
}